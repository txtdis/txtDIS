package ph.txtdis.service;

import static ph.txtdis.type.UserType.MANAGER;
import static ph.txtdis.type.UserType.STOCK_CHECKER;
import static ph.txtdis.type.UserType.STORE_KEEPER;

import java.util.List;

import ph.txtdis.dto.Billable;
import ph.txtdis.dto.BillableDetail;
import ph.txtdis.dto.Item;
import ph.txtdis.dto.ReceivingDetail;
import ph.txtdis.exception.AlreadyReceivedBookingIdException;
import ph.txtdis.exception.NotAllowedOffSiteTransactionException;
import ph.txtdis.info.Information;

public abstract class AbstractReceivingReportService //
		extends AbstractBillableService //
		implements ReceivingReportService {

	private BillableDetail receivingDetail;

	private List<BillableDetail> originalDetails;

	@Override
	public String getAlternateName() {
		return "R/R";
	}

	@Override
	public String getHeaderText() {
		return "Receiving Report";
	}

	@Override
	public String getReceivingPrompt() {
		return "Receiving Report No.";
	}

	@Override
	public String getReferencePrompt() {
		return "Reference No.";
	}

	@Override
	public List<? extends ReceivingDetail> getOriginalDetails() {
		return originalDetails;
	}

	@Override
	@SuppressWarnings("unchecked")
	public BillableDetail getReceivingDetail() {
		return receivingDetail;
	}

	@Override
	public String getSavingInfo() {
		return getModuleId() + getReceivingId();
	}

	@Override
	public Long getSpunId() {
		return isNew() ? null : getReceivingId();
	}

	@Override
	public String getSpunModule() {
		return "receivingReport";
	}

	@Override
	public boolean isAppendable() {
		return isNew();
	}

	@Override
	public boolean isNew() {
		return getReceivedOn() == null;
	}

	@Override
	public boolean isSalesOrderReturnable() {
		return isNew() && isUnbilledReceivingReport() && canReceiveSalesOrder();
	}

	private boolean isUnbilledReceivingReport() {
		return get().getBilledOn() == null;
	}

	private boolean canReceiveSalesOrder() {
		return canModifyReceivingReport() || credentialService.isUser(STORE_KEEPER);
	}

	private boolean canModifyReceivingReport() {
		return credentialService.isUser(MANAGER) || credentialService.isUser(STOCK_CHECKER);
	}

	@Override
	public boolean isReceivingReportModifiable() {
		return isUnbilledReceivingReport() && !isNew() && canModifyReceivingReport();
	}

	@Override
	public void save() throws Information, Exception {
		setReceivedByUser();
		super.save();
	}

	@Override
	public void setItem(Item item) {
		this.item = item;
	}

	@Override
	public void setReceivingDetail(ReceivingDetail detail) {
		receivingDetail = (BillableDetail) detail;
	}

	@Override
	public void updateUponReferenceIdValidation(long id) throws Exception {
		if (isNewAndOffSite())
			throw new NotAllowedOffSiteTransactionException();
		Billable reference = validateBooking(id);
		updateBasedOnBooking(reference);
	}

	protected Billable validateBooking(Long id) throws Exception {
		Billable reference = findReference(id);
		confirmBookingExists(id.toString(), reference);
		confirmBookingHasBeenPicked(id.toString(), reference);
		confirmBookingIsStillReceivable(id, reference);
		return reference;
	}

	private Billable findReference(long id) throws Exception {
		return findBillable("/booking?id=" + id);
	}

	private void confirmBookingIsStillReceivable(Long id, Billable b) throws Exception {
		Long receivingId = b.getReceivingId();
		if (receivingId != null)
			throw new AlreadyReceivedBookingIdException(getReferencePrompt(b), id.toString(), receivingId);
	}

	private void updateBasedOnBooking(Billable b) {
		originalDetails = b.getDetails();
		b.setDetails(null);
		set(b);
	}
}