package ph.txtdis.mgdc.gsm.service.server;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ph.txtdis.dto.Billable;
import ph.txtdis.dto.BillableDetail;
import ph.txtdis.dto.Bom;
import ph.txtdis.mgdc.gsm.domain.BillableEntity;
import ph.txtdis.mgdc.gsm.domain.ItemEntity;
import ph.txtdis.service.RestClientService;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.List;

import static java.math.BigDecimal.ZERO;
import static java.util.stream.Collectors.toList;
import static java.util.stream.StreamSupport.stream;
import static ph.txtdis.type.PartnerType.OUTLET;
import static ph.txtdis.type.PriceType.DEALER;
import static ph.txtdis.util.DateTimeUtils.toDate;
import static ph.txtdis.util.NumberUtils.isZero;

@Service("invoiceService")
public class InvoiceServiceImpl //
	extends AbstractSpunSavedBillingService //
	implements InvoiceService {

	private static final String BILLABLE = "billable";

	@Autowired
	private RestClientService<Billable> restClientService;

	@Autowired
	private BomService bomService;

	@Autowired
	private PriceService priceService;

	@Override
	protected BillableEntity create(Billable b) {
		return forCancelation(b) ? cancelledEntity(b) : updateLoadOrderAndCreateInvoice(b);
	}

	private boolean forCancelation(Billable b) {
		return b.getBookingId() == null && b.getDetails().isEmpty();
	}

	private BillableEntity cancelledEntity(Billable b) {
		BillableEntity e = orderNoOnlyEntity(b);
		e = setCancelledData(e);
		return setOrderNoAndBillingData(e, b);
	}

	private BillableEntity updateLoadOrderAndCreateInvoice(Billable b) {
		BillableEntity e = super.create(b);
		return updateLoadOrderQtyAndNegateBookingIdIfFromLoadOrderThenSetBillingData(repository, e, b);
	}

	private BillableEntity setCancelledData(BillableEntity e) {
		e.setRemarks(remarks(e));
		e.setFullyPaid(true);
		return e;
	}

	private String remarks(BillableEntity e) {
		String remarks = e.getRemarks();
		if (remarks != null && !remarks.trim().isEmpty())
			remarks = remarks + "\n";
		else
			remarks = "";
		return remarks + CANCELLED;
	}

	@Override
	public List<Bom> extractAll(Long itemId, String itemName, BigDecimal qty) {
		return bomService.extractAll(itemId, itemName, qty);
	}

	@Override
	public List<Billable> findAllBilledButUnpicked() {
		List<BillableEntity> l = repository
			.findByCustomerTypeAndBilledOnNotNullAndNumIdGreaterThanAndRmaNullAndPickingNullOrderByOrderDateAscIdAsc(
				OUTLET, 0L);
		return toModels(l);
	}

	@Override
	public List<BillableEntity> findAllById(List<Long> ids) {
		Iterable<BillableEntity> i = repository.findAll(ids);
		return i == null ? null : stream(i.spliterator(), false).collect(toList());
	}

	@Override
	public List<Billable> findAllOutletBillings() {
		List<BillableEntity> l = repository
			.findByCustomerTypeAndBilledOnNotNullAndNumIdGreaterThanAndRmaNullOrderByOrderDateAscIdAsc(OUTLET, 0L);
		return toModels(l);
	}

	@Override
	protected BillableEntity firstEntity() {
		return repository.findFirstByBilledOnNotNullAndNumIdGreaterThanOrderByIdAsc(0L);
	}

	@Override
	public void importAll() throws Exception {
		post(toEntities(billables()));
	}

	private List<Billable> billables() throws Exception {
		return restClientService.module(BILLABLE).getList();
	}

	@Override
	public List<Billable> findAllInvalidInvoices(List<Long> ids) {
		List<BillableEntity> l = repository.findByNumIdInAndIsValidNull(ids);
		return toModels(l);
	}

	@Override
	protected BillableEntity lastEntity() {
		return repository.findFirstByBilledOnNotNullAndNumIdGreaterThanOrderByIdDesc(0L);
	}

	@Override
	protected BillableEntity nextEntity(Long id) {
		return repository.findFirstByBilledOnNotNullAndNumIdGreaterThanAndIdGreaterThanOrderByIdAsc(0L, id);
	}

	@Override
	protected BillableEntity previousEntity(Long id) {
		return repository.findFirstByBilledOnNotNullAndNumIdGreaterThanAndIdLessThanOrderByIdDesc(0L, id);
	}

	@Override
	@Transactional
	public Billable save(Billable b) {
		try {
			b = super.save(b);
			return saveToEdms(b);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public Billable saveToEdms(Billable b) throws Exception {
		restClientService.module(BILLABLE).save(expandedBomDetails(b));
		return b;
	}

	private Billable expandedBomDetails(Billable b) {
		b.setDetails(getBomExpandedDetails(b));
		return b;
	}

	@Override
	public BillableDetail toDetail(Billable b, BillableDetail bd, Bom bom) {
		ItemEntity i = itemService.findEntityByPrimaryKey(bom.getId());
		BillableDetail d = InvoiceService.super.toDetail(b, bd, bom);
		d.setItemVendorNo(i.getVendorId());
		d.setUom(bd.getUom());
		d.setPriceValue(priceService.getLatestValue(DEALER.toString(), i, b.getOrderDate()));
		d.setQtyPerCase(itemService.getCountPerCase(i));
		d.setReturnedQty(bomMultipliedQty(bom, bd.getReturnedQty()));
		d.setSoldQty(bomMultipliedQty(bom, bd.getSoldQty()));
		return d;
	}

	private BigDecimal bomMultipliedQty(Bom bom, BigDecimal qty) {
		return qty.multiply(bom.getQty());
	}

	@Override
	protected BillableEntity update(BillableEntity e, Billable b) {
		e = setOrderNoAndBillingData(e, b);
		if (isForCancellation(e, b))
			return setCancelledData(e);
		updateLoadOrderQtyAndNegateBookingIdIfFromLoadOrderThenSetBillingData(repository, e, b);
		return super.update(e, b);
	}

	private boolean isForCancellation(BillableEntity e, Billable b) {
		if (e.getRemarks() != null && e.getRemarks().contains(CANCELLED))
			return false;
		return voided(b) || allReturned(b);
	}

	private boolean voided(Billable b) {
		boolean voided = b.getBookingId() == null && b.getCustomerId() == null && b.getCustomerName() == null;
		return voided;
	}

	private boolean allReturned(Billable b) {
		if (b == null || b.getDetails().isEmpty())
			return true;
		BigDecimal qty = b.getDetails().stream().map(BillableDetail::getFinalQty).reduce(ZERO, BigDecimal::add);
		return isZero(qty);
	}

	@Override
	public void updateItemReturnPayment(String[] s) {
		BillableEntity b = repository.findOne(Long.valueOf(s[1]));
		b.setOrderDate(toDate(s[2]));
		b.setPrefix(s[3]);
		b.setNumId(Long.valueOf(s[4]));
		b.setSuffix(s[5]);
		b.setBilledBy(s[6]);
		b.setBilledOn(ZonedDateTime.parse(s[7]));
		b.setUnpaidValue(ZERO);
		b.setFullyPaid(true);
		repository.save(b);
	}
}