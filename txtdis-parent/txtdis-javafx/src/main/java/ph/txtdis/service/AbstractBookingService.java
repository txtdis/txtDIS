package ph.txtdis.service;

import static java.time.LocalDate.now;
import static ph.txtdis.type.ScriptType.BILLING_APPROVAL;
import static ph.txtdis.type.UserType.MANAGER;
import static ph.txtdis.util.DateTimeUtils.toDateDisplay;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ph.txtdis.dto.Billable;
import ph.txtdis.dto.BillableDetail;
import ph.txtdis.dto.Customer;
import ph.txtdis.dto.EntityDecisionNeeded;
import ph.txtdis.exception.NotAllowedOffSiteTransactionException;
import ph.txtdis.exception.NotFullyPaidCashBillableException;
import ph.txtdis.info.Information;
import ph.txtdis.info.SuccessfulSaveInfo;
import ph.txtdis.type.QualityType;
import ph.txtdis.type.ScriptType;

@Service("salesOrderService")
public abstract class AbstractBookingService extends AbstractBillableService implements BookingService {

	@Autowired
	private ScriptService scriptService;

	@Autowired
	private TotaledBillableService totalService;

	@Autowired
	private VatService vatService;

	protected Customer customer;

	@Override
	public boolean canApprove() {
		return !isNew() && credentialService.isUser(MANAGER);
	}

	@Override
	public boolean canInvalidSalesOrderBeOverriden() {
		return credentialService.isUser(MANAGER) //
				&& getIsValid() != null //
				&& getIsValid() == false //
				&& get().getPickListId() == null;
	}

	@Override
	public String getAlternateName() {
		return "S/O";
	}

	@Override
	public String getDecisionTag(Boolean isValid) {
		String s = "VALID";
		if (isValid == null)
			return "";
		if (!isValid)
			s = "IN" + s;
		return "[" + s + ": " + credentialService.username() + " - " + toDateDisplay(now()) + "] ";
	}

	@Override
	public String getIdPrompt() {
		return "S/I(D/R) No.";
	}

	@Override
	public LocalDate getOrderDate() {
		if (get().getOrderDate() == null)
			setOrderDate(nextWorkDay());
		return get().getOrderDate();
	}

	@Override
	public String getReferencePrompt() {
		return "S/O No.";
	}

	@Override
	public ScriptService getScriptService() {
		return scriptService;
	}

	@Override
	public <T extends EntityDecisionNeeded<Long>> ScriptType getScriptType(T d) {
		return BILLING_APPROVAL;
	}

	@Override
	public BigDecimal getVat() {
		return vatService.getVat(getTotalValue());
	}

	@Override
	public BigDecimal getVatable() {
		return vatService.getVatable(getTotalValue());
	}

	@Override
	public void invalidateAwaitingApproval(String badCreditMessage) {
		updatePerValidity(false, badCreditMessage.replace("\n", " "));
	}

	@Override
	public boolean isAppendable() {
		return isNew();
	}

	@Override
	public void overrideInvalidation() throws Information, Exception {
		set(setDecisionStatus(get(), null, ""));
		save();
	}

	@Override
	protected QualityType quality() {
		return QualityType.GOOD;
	}

	@Override
	public void save() throws Information, Exception {
		set(save(get()));
		scriptService.saveScripts();
		throw new SuccessfulSaveInfo(getSavingInfo());
	}

	@Override
	public String getSavingInfo() {
		if (getOrderNo().equals("0"))
			return getAlternateName() + " invalidation";
		return super.getSavingInfo();
	}

	@Override
	public void searchForCustomer(String name) {
		try {
			customerService.search(name);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void updateSummaries(List<BillableDetail> items) {
		setDetails(items);
		set(totalService.updateTotals(get()));
	}

	@Override
	public void updateUponCustomerIdValidation(Long id) throws Exception {
		if (isNewAndOffSite())
			throw new NotAllowedOffSiteTransactionException();
		if (!credentialService.isUser(MANAGER) || !isNew())
			return;
		verifyUserAuthorization();
		verifyAllPickedSalesOrderHaveBeenBilled(credentialService.username(), getOrderDate());
		verifyAllCashBillablesHaveBeenFullyPaid();
	}

	@Override
	public String username() {
		return credentialService.username();
	}

	protected void verifyAllCashBillablesHaveBeenFullyPaid() throws Exception {
		Billable b = findBillable("/notFullyPaidCOD?seller=" + getSeller() + "&upTo=" + getOrderDate());
		if (b != null) {
			reset();
			throw new NotFullyPaidCashBillableException(b.getOrderNo());
		}
	}
}
