package ph.txtdis.dyvek.service.server;

import static java.math.BigDecimal.ZERO;
import static java.util.stream.Collectors.toList;
import static ph.txtdis.type.PartnerType.OUTLET;
import static ph.txtdis.util.NumberUtils.zeroIfNull;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ph.txtdis.dyvek.domain.BillableEntity;
import ph.txtdis.dyvek.domain.BillableReferenceEntity;
import ph.txtdis.dyvek.domain.CustomerEntity;
import ph.txtdis.dyvek.domain.DeliveryDetailEntity;
import ph.txtdis.dyvek.domain.ItemEntity;
import ph.txtdis.dyvek.model.Billable;
import ph.txtdis.dyvek.model.BillableDetail;
import ph.txtdis.dyvek.repository.BillableReferenceRepository;
import ph.txtdis.dyvek.repository.ClientBillRepository;

@Service("clientBillService")
public class ClientBillServiceImpl //
		extends AbstractBillService<ClientBillRepository> //
		implements ClientBillService {

	@Autowired
	private BillableReferenceRepository referenceRepository;

	@Override
	protected BigDecimal adjustmentPrice(DeliveryDetailEntity d) {
		return d == null ? ZERO : zeroIfNull(d.getBillAdjustmentPriceValue());
	}

	@Override
	protected BigDecimal adjustmentQty(DeliveryDetailEntity d) {
		return d == null ? ZERO : zeroIfNull(d.getBillAdjustmentQty());
	}

	@Override
	public List<Billable> findAllOpen() {
		List<BillableReferenceEntity> l = //
				referenceRepository.findByReferenceCustomerTypeAndBillingNullOrderByReferenceOrderDateAsc(OUTLET);
		return l == null ? null //
				: l.stream() //
						.map(r -> toBillable(r)) //
						.collect(toList());
	}

	private Billable toBillable(BillableReferenceEntity r) {
		BillableEntity delivery = r.getDelivery();
		BillableEntity sales = r.getReference();
		Billable b = new Billable();
		b.setId(r.getId());
		b.setOrderDate(delivery.getOrderDate());
		b.setVendor(customer(delivery));
		b.setClient(customer(sales));
		b.setDeliveryNo(delivery.getOrderNo());
		b.setSalesNo(sales.getOrderNo());
		b.setItem(item(delivery));
		b.setTotalQty(delivery.getTotalQty());
		b.setPriceValue(priceValue(sales));
		return b;
	}

	@Override
	public Billable findByBillId(String no) {
		BillableEntity e = repository.findByBillsNotNullAndOrderNo(no);
		return toModel(e);
	}

	@Override
	public Billable findUnbilledById(Long id) {
		BillableEntity b = referenceRepository.findOne(id).getReference();
		List<BillableReferenceEntity> l = referenceRepository.findByReferenceCustomerAndReferenceOrderNo(b.getCustomer(), b.getOrderNo());
		return toBillable(l, b);
	}

	private Billable toBillable(List<BillableReferenceEntity> l, BillableEntity e) {
		Billable b = new Billable();
		b.setSalesNo(salesNo(l));
		b.setClient(customer(e));
		b.setItem(item(e));
		b.setItemDescription(itemDescription(e));
		b.setBillings(details(l));
		return b;
	}

	private String itemDescription(BillableEntity e) {
		ItemEntity i = e.getItem();
		return i == null ? null : i.getDescription();
	}

	private List<BillableDetail> details(List<BillableReferenceEntity> l) {
		return l == null ? null
				: l.stream() //
						.map(r -> toDetail(r)) //
						.collect(toList());
	}

	@Override
	protected BillableEntity firstEntity() {
		return repository.findFirstByBillsNotNullOrderByIdAsc();
	}

	@Override
	protected BillableEntity nextEntity(Long id) {
		return repository.findFirstByBillsNotNullAndIdGreaterThanOrderByIdAsc(id);
	}

	@Override
	protected BillableEntity lastEntity() {
		return repository.findFirstByBillsNotNullOrderByIdDesc();
	}

	@Override
	protected BillableEntity previousEntity(Long id) {
		return repository.findFirstByBillsNotNullAndIdLessThanOrderByIdDesc(id);
	}

	@Override
	public List<Billable> search(String bill) {
		List<BillableEntity> l = repository.findByBillsNotNullAndOrderNoContainingIgnoreCase(bill);
		return toModels(l);
	}

	@Override
	public Billable toModel(BillableEntity e) {
		super.toModel(e);
		if (e == null)
			return null;
		Billable b = new Billable();
		b.setId(e.getId());
		b.setBillNo(e.getOrderNo());
		b.setSalesNo(salesNo(e));
		b.setBillDate(e.getOrderDate());
		b.setClient(customer(e));
		b.setItem(item(e));
		b.setItemDescription(itemDescription(e));
		b.setRemarks(e.getRemarks());
		b.setBillings(billings(e));
		return setCreationData(b, e);
	}

	private String salesNo(BillableEntity e) {
		List<BillableReferenceEntity> l = e.getBills();
		return salesNo(l);
	}

	private String salesNo(List<BillableReferenceEntity> l) {
		if (l != null && l.size() > 0)
			return l.get(0).getReference().getOrderNo();
		return null;
	}

	private List<BillableDetail> billings(BillableEntity e) {
		List<BillableReferenceEntity> l = e.getBills();
		return l == null ? null //
				: l.stream() //
						.map(r -> toDetail(r)) //
						.filter(d -> d != null) //
						.collect(toList());
	}

	@Override
	protected BillableDetail toDetail(BillableReferenceEntity r) {
		BillableEntity delivery = r.getDelivery();
		BillableEntity sales = r.getReference();
		BillableDetail d = new BillableDetail();
		d.setId(r.getId());
		d.setOrderNo(delivery.getOrderNo());
		d.setOrderDate(delivery.getOrderDate());
		d.setPriceValue(priceValue(sales));
		d.setQty(r.getQty());
		d.setAssignedQty(r.getQty());
		return d;
	}

	@Override
	public BillableEntity toEntity(Billable b) {
		if (b == null)
			return null;
		BillableEntity e = new BillableEntity();
		e.setOrderDate(b.getOrderDate());
		e.setOrderNo(b.getBillNo());
		e.setCustomer(customer(b));
		e.setItem(item(b));
		e.setRemarks(b.getRemarks());
		e.setReferences(references(e, b));
		return e;
	}

	private CustomerEntity customer(Billable b) {
		return customerService.findEntityByName(b.getClient());
	}

	private List<BillableReferenceEntity> references(BillableEntity e, Billable b) {
		List<BillableDetail> l = b.getBillings();
		return l.isEmpty() ? null //
				: l.stream() //
						.map(d -> referenceRepository.findOne(d.getId())) //
						.filter(r -> r != null) //
						.map(r -> setBilling(r, e)) //
						.collect(toList());
	}

	private BillableReferenceEntity setBilling(BillableReferenceEntity r, BillableEntity e) {
		r.setBilling(e);
		return r;
	}

	@Override
	protected BigDecimal totalValue(BillableEntity e) {
		try {
			return e.getReferences().stream() //
					.map(r -> r.getReference()) //
					.map(b -> super.totalValue(b)) //
					.reduce(ZERO, BigDecimal::add);
		} catch (Exception ex) {
			return ZERO;
		}
	}

	@Override
	protected List<BillableEntity> deliveries(BillableEntity e) {
		return null;
	}
}