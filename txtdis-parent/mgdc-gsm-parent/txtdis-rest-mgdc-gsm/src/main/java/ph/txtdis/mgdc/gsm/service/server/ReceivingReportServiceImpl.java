package ph.txtdis.mgdc.gsm.service.server;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ph.txtdis.dto.Billable;
import ph.txtdis.dto.BillableDetail;
import ph.txtdis.dto.Bom;
import ph.txtdis.exception.NotFoundException;
import ph.txtdis.mgdc.gsm.domain.BillableDetailEntity;
import ph.txtdis.mgdc.gsm.domain.BillableEntity;
import ph.txtdis.mgdc.gsm.repository.BillableRepository;
import ph.txtdis.mgdc.gsm.repository.ReceivingReportRepository;
import ph.txtdis.type.PartnerType;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.List;

import static java.util.Arrays.asList;
import static ph.txtdis.type.PartnerType.EX_TRUCK;
import static ph.txtdis.type.PartnerType.OUTLET;

@Service("receivingReportService")
public class ReceivingReportServiceImpl //
	extends AbstractSpunSavedBillableService //
	implements ReceivableService,
	ReceivingReportService,
	QtyToLoadOrderDetailItemMappingService {

	private static final List<PartnerType> DELIVERED_ROUTES = asList(EX_TRUCK, OUTLET);

	@Autowired
	private ReceivingReportRepository receivingRepository;

	@Autowired
	private LoadOrderService loadOrderService;

	@Autowired
	private PickListService pickingService;

	@Autowired
	private BomService bomService;

	@Override
	public List<Bom> extractAll(Long itemId, String itemName, BigDecimal qty) {
		return bomService.extractAll(itemId, itemName, qty);
	}

	@Override
	public Billable findByBookingId(Long id) {
		return loadOrderService.findAsReference(id);
	}

	@Override
	public Billable findByReferenceId(Long id) throws NotFoundException {
		BillableEntity e = receivingRepository.findByCustomerTypeInAndRmaNullAndReceivingId(DELIVERED_ROUTES, id);
		return throwExceptionIfNotFound("R/R No.", e, id);
	}

	@Override
	protected BillableEntity firstEntity() {
		return receivingRepository.findFirstByCustomerTypeInAndRmaNullAndReceivingIdNotNullOrderByIdAsc
			(DELIVERED_ROUTES);
	}

	@Override
	protected BillableEntity nextEntity(Long id) {
		return receivingRepository
			.findFirstByCustomerTypeInAndRmaNullAndReceivingIdNotNullAndIdGreaterThanOrderByIdAsc(DELIVERED_ROUTES, id);
	}

	@Override
	protected BillableEntity lastEntity() {
		return receivingRepository
			.findFirstByCustomerTypeInAndRmaNullAndReceivingIdNotNullOrderByIdDesc(DELIVERED_ROUTES);
	}

	@Override
	protected BillableEntity previousEntity(Long id) {
		return receivingRepository
			.findFirstByCustomerTypeInAndRmaNullAndReceivingIdNotNullAndIdLessThanOrderByIdDesc(DELIVERED_ROUTES, id);
	}

	@Override
	@Transactional
	public BillableEntity post(BillableEntity e) {
		e = super.post(e);
		pickingService.postToEdms(e);
		return e;
	}

	@Override
	public BillableDetailEntity setTheTotalOfTheMappedEntityAndModelDetailsItemQuantities(BillableDetailEntity e,
	                                                                                      BillableDetail b) {
		e.setReturnedQty(b.getReturnedQty());
		return e;
	}

	@Override
	protected BillableEntity update(BillableEntity e, Billable b) {
		e = setReceivingData(e, b, repository);
		e = addReceivedQtyToDetails(e, b);
		addReceivedQtyToLoadOrderIfExTruck(e, b);
		return updateTotals(e, b);
	}

	@Override
	public BillableEntity setReceivingData(BillableEntity e, Billable b, BillableRepository r) {
		e = ReceivableService.super.setReceivingData(e, b, r);
		return setModifiedReceivingData(e, b);
	}

	private BillableEntity addReceivedQtyToDetails(BillableEntity e, Billable b) {
		return updateDetailQty(e, b);
	}

	private void addReceivedQtyToLoadOrderIfExTruck(BillableEntity e, Billable b) {
		updateLoadOrderDetailQty(repository, b);
	}

	private BillableEntity setModifiedReceivingData(BillableEntity e, Billable b) {
		if (b.getReceivingModifiedBy() == null || e.getReceivingModifiedBy() != null)
			return e;
		e.setReceivingModifiedBy(b.getReceivingModifiedBy());
		e.setReceivingModifiedOn(ZonedDateTime.now());
		return e;
	}
}