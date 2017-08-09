package ph.txtdis.mgdc.gsm.service;

import org.springframework.beans.factory.annotation.Autowired;
import ph.txtdis.dto.Billable;
import ph.txtdis.dto.BillableDetail;
import ph.txtdis.dto.Keyed;
import ph.txtdis.dto.ReceivingDetail;
import ph.txtdis.exception.AlreadyReceivedBookingIdException;
import ph.txtdis.exception.NotFoundException;
import ph.txtdis.info.Information;
import ph.txtdis.mgdc.gsm.dto.Item;
import ph.txtdis.mgdc.service.TotaledBillableService;

import java.util.List;

import static java.util.stream.Collectors.toList;
import static ph.txtdis.type.UserType.*;
import static ph.txtdis.util.NumberUtils.isZero;
import static ph.txtdis.util.UserUtils.isUser;

public abstract class AbstractReceivingReportService //
	extends AbstractBillableService //
	implements ReceivingReportService {

	@Autowired
	private TotaledBillableService totalService;

	private BillableDetail receivingDetail;

	private List<BillableDetail> bookedDetails;

	@Override
	@SuppressWarnings("unchecked")
	public Keyed<Long> findByModuleKey(String key) throws Exception {
		try {
			Long.valueOf(key);
			return super.findByModuleKey(key);
		} catch (Exception e) {
			throw new NotFoundException(getAbbreviatedModuleNoPrompt() + key);
		}
	}

	@Override
	public String getAlternateName() {
		return "R/R";
	}

	@Override
	public List<BillableDetail> getDetails() {
		try {
			return get().getDetails().stream().filter(d -> !isZero(d.getReturnedQty())).collect(toList());
		} catch (Exception e) {
			return null;
		}
	}

	@Override
	public String getHeaderName() {
		return "Receiving Report";
	}

	@Override
	public String getReceivingPrompt() {
		return "Receiving Report No.";
	}

	@Override
	public String getReferencePrompt() {
		return "Load/Sales Order No.";
	}

	@Override
	public List<? extends ReceivingDetail> getPickedDetails() {
		return bookedDetails;
	}

	@Override
	@SuppressWarnings("unchecked")
	public BillableDetail getReceivingDetail() {
		return receivingDetail;
	}

	@Override
	public void setReceivingDetail(ReceivingDetail detail) {
		receivingDetail = (BillableDetail) detail;
	}

	@Override
	public String getSavingInfo() {
		return getAbbreviatedModuleNoPrompt() + getModuleNo();
	}

	@Override
	public String getModuleNo() {
		return getReceivingId().toString();
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
		return canModifyReceivingReport() || isUser(STORE_KEEPER);
	}

	private boolean canModifyReceivingReport() {
		return isUser(MANAGER) || isUser(STOCK_CHECKER);
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
	public void updateSummaries(List<BillableDetail> items) {
		super.updateSummaries(items);
		set(totalService.updateFinalTotals(get()));
	}

	@Override
	public void updateUponReferenceIdValidation(long id) throws Exception {
		Billable reference = validateBooking(id);
		updateBasedOnBooking(reference);
	}

	protected Billable validateBooking(Long id) throws Exception {
		Billable booking = getRestClientService().module(getModuleName()).getOne("/booking?id=" + id);
		confirmBookingExists(id.toString(), booking);
		confirmBookingHasBeenPicked(id.toString(), booking);
		confirmBookingIsStillReceivable(id, booking);
		return booking;
	}

	private void updateBasedOnBooking(Billable b) {
		bookedDetails = b.getDetails();
		b.setDetails(null);
		set(b);
	}

	@Override
	public String getModuleName() {
		return "receivingReport";
	}

	protected void confirmBookingIsStillReceivable(Long bookingId, Billable b) throws Exception {
		confirmItemsNotBeenReceived(bookingId, b);
	}

	private void confirmItemsNotBeenReceived(Long id, Billable b) throws Exception {
		Long receivingId = b.getReceivingId();
		if (receivingId != null)
			throw new AlreadyReceivedBookingIdException(getReferencePrompt(b), id.toString(), receivingId);
	}
}