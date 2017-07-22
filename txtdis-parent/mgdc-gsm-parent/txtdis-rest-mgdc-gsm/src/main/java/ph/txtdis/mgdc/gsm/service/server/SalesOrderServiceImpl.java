package ph.txtdis.mgdc.gsm.service.server;

import static java.util.Arrays.asList;
import static ph.txtdis.type.PartnerType.OUTLET;
import static ph.txtdis.type.PartnerType.VENDOR;
import static ph.txtdis.util.DateTimeUtils.toDateDisplay;

import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ph.txtdis.dto.Billable;
import ph.txtdis.mgdc.gsm.domain.BillableDetailEntity;
import ph.txtdis.mgdc.gsm.domain.BillableEntity;
import ph.txtdis.mgdc.gsm.repository.BillingDetailRepository;
import ph.txtdis.mgdc.gsm.repository.SalesOrderRepository;
import ph.txtdis.service.CredentialService;

@Service("salesOrderService")
public class SalesOrderServiceImpl //
		extends AbstractBookingService<SalesOrderRepository> //
		implements SalesOrderService {

	private static final String CREATED_NEW_ITEM_DETAILS = "CREATED NEW ITEM DETAILS";

	@Autowired
	private BillingDetailRepository detailRepository;

	@Autowired
	private CredentialService credentialService;

	@Override
	public List<BillableEntity> findAllPicked() {
		return bookingRepository.findByPickingNotNullOrderByPickingPickDateAscPickingIdAsc();
	}

	@Override
	public List<BillableEntity> findAllPickedWithReturns() {
		return bookingRepository.findByPickingNotNullAndReceivedOnNotNullOrderByPickingPickDateAscPickingIdAsc();
	}

	@Override
	public BillableEntity findEntityByBookingNo(String key) {
		return bookingRepository.findFirstByCustomerTypeInAndBookingIdAndRmaNullOrderById( //
				asList(OUTLET), //
				Long.valueOf(key));
	}

	@Override
	public Billable findUnbilledPickedBooking(LocalDate d) {
		BillableEntity e = bookingRepository.findFirstByNumIdNullAndRmaNullAndCustomerTypeAndPickingNotNullAndOrderDateBetween( //
				OUTLET, //
				goLive(), //
				billingCutoff(d));
		return toBookingIdOnlyBillable(e);
	}

	@Override
	public Billable findUnpicked(LocalDate d) {
		BillableEntity e = bookingRepository.findFirstByNumIdNullAndRmaNullAndCustomerTypeNotAndPickingNullAndOrderDateBetween( //
				VENDOR, //
				goLive(), //
				billingCutoff(d));
		return toBookingIdOnlyBillable(e);
	}

	@Override
	protected BillableEntity firstEntity() {
		return bookingRepository.findFirstByCustomerTypeAndRmaNullAndBookingIdGreaterThanOrderByIdAsc(OUTLET, 0L);
	}

	@Override
	protected BillableEntity lastEntity() {
		return bookingRepository.findFirstByCustomerTypeAndRmaNullAndBookingIdGreaterThanOrderByIdDesc(OUTLET, 0L);
	}

	@Override
	protected BillableEntity nextEntity(Long id) {
		return bookingRepository.findFirstByCustomerTypeAndRmaNullAndIdGreaterThanAndBookingIdGreaterThanOrderByIdAsc(OUTLET, id, 0L);
	}

	@Override
	protected BillableEntity previousEntity(Long id) {
		return bookingRepository.findFirstByCustomerTypeAndRmaNullAndIdLessThanAndBookingIdGreaterThanOrderByIdDesc(OUTLET, id, 0L);
	}

	@Override
	public Billable toModel(BillableEntity e) {
		Billable b = super.toModel(e);
		b.setCanChangeDetails(canChangeDetails(e));
		b.setDetailsChanged(detailsChanged(e));
		return b;

	}

	private boolean canChangeDetails(BillableEntity e) {
		return e.getRemarks() == null ? false : e.getRemarks().startsWith(EXTRACTED_FROM_INVALIDATED_S_I_D_R_NO);
	}

	private boolean detailsChanged(BillableEntity e) {
		return e.getRemarks() == null ? false : e.getRemarks().startsWith(CREATED_NEW_ITEM_DETAILS);
	}

	@Override
	protected BillableEntity update(Billable b) {
		if (b.isDetailsChanged())
			deleteDetails(b);
		return super.update(b);
	}

	private void deleteDetails(Billable b) {
		List<BillableDetailEntity> l = detailRepository.findByBillingId(b.getId());
		detailRepository.delete(l);
	}

	@Override
	protected BillableEntity update(BillableEntity e, Billable b) {
		return b.isDetailsChanged() ? changeDetailsAndUpdateRemarks(e, b) : super.update(e, b);
	}

	private BillableEntity changeDetailsAndUpdateRemarks(BillableEntity e, Billable b) {
		e.setRemarks(newDetailRemarks(b));
		e.setDetails(entityDetails(e, b));
		return e;
	}

	private String newDetailRemarks(Billable b) {
		return CREATED_NEW_ITEM_DETAILS + ": " //
				+ credentialService.username() + " - " //
				+ toDateDisplay(LocalDate.now()) //
				+ remarks(b);
	}

	private String remarks(Billable b) {
		String r = b.getRemarks();
		return b == null ? "" : "\n" + r;
	}
}