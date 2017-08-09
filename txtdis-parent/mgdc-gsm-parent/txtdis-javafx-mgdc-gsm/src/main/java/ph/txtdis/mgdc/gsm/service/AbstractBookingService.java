package ph.txtdis.mgdc.gsm.service;

import org.springframework.beans.factory.annotation.Autowired;
import ph.txtdis.dto.Billable;
import ph.txtdis.dto.BillableDetail;
import ph.txtdis.dto.DecisionNeededValidatedCreatedKeyed;
import ph.txtdis.exception.NotFoundException;
import ph.txtdis.exception.NotFullyPaidCashBillableException;
import ph.txtdis.info.Information;
import ph.txtdis.mgdc.gsm.dto.Customer;
import ph.txtdis.mgdc.service.TotaledBillableService;
import ph.txtdis.type.QualityType;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static ph.txtdis.type.UserType.MANAGER;
import static ph.txtdis.util.TextUtils.blankIfNullElseAddCarriageReturn;
import static ph.txtdis.util.UserUtils.isUser;

public abstract class AbstractBookingService //
	extends AbstractBillableService //
	implements BookingService {

	@Autowired
	protected TotaledBillableService totalService;

	protected Customer customer;

	@Autowired
	private CustomerValidationService customerValidationService;

	@Autowired
	private VatService vatService;

	@Override
	public boolean canApprove() {
		return !isNew() && isUser(MANAGER);
	}

	@Override
	public boolean canInvalidSalesOrderBeOverriden() {
		return isUser(MANAGER) //
			&& getIsValid() != null //
			&& getIsValid() == false //
			&& get().getPickListId() == null;
	}

	@Override
	@SuppressWarnings("unchecked")
	public Billable findByModuleId(Long id) throws Exception {
		Billable b = (Billable) super.findByModuleId(id);
		if (b == null)
			throw new NotFoundException(getAbbreviatedModuleNoPrompt() + id);
		return b;
	}

	@Override
	public <T extends DecisionNeededValidatedCreatedKeyed<Long>> String addDecisionToRemarks(T t,
	                                                                                         Boolean isValid,
	                                                                                         String remarks) {
		String s = blankIfNullElseAddCarriageReturn(t.getRemarks());
		return s + getDecisionTag(isValid, "IN", "VALID", remarks);
	}

	@Override
	public String getDecisionTag(Boolean isValid, String prefix, String decision, String remarks) {
		return isValid == null ? "" : super.getDecisionTag(isValid, prefix, decision, remarks);
	}

	@Override
	public String getIdPrompt() {
		return "S/I(D/R) No.";
	}

	@Override
	public String getReferencePrompt() {
		return "S/O No.";
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
	public void reset() {
		customer = null;
		super.reset();
	}

	@Override
	public String getSavingInfo() {
		if (getOrderNo().equals("0"))
			return getAlternateName() + " invalidation";
		return super.getSavingInfo();
	}

	@Override
	public String getAlternateName() {
		return "S/O";
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
		super.updateSummaries(items);
		set(totalService.updateInitialTotals(get()));
	}

	@Override
	public void updateUponCustomerIdValidation(Long id) throws Exception {
		customer = customerValidationService.validate(id, getOrderDate());
		if (!isNew() || isUser(MANAGER))
			return;
		verifyUserAuthorization();
		verifyAllSalesOrderHaveBeenPicked(getRestClientService(), getOrderDate());
		verifyAllPickedSalesOrderHaveBeenBilled(getRestClientService(), getOrderDate());
		verifyAllCODsHaveBeenFullyPaid();
	}

	@Override
	public LocalDate getOrderDate() {
		if (get().getOrderDate() == null)
			setOrderDate(nextWorkDay());
		return get().getOrderDate();
	}

	protected abstract void verifyUserAuthorization() throws Exception;

	protected void verifyAllCODsHaveBeenFullyPaid() throws Exception {
		Billable b = findBillable("/notFullyPaidCOD?upTo=" + getOrderDate());
		if (b != null)
			throw new NotFullyPaidCashBillableException(b.getOrderNo());
	}
}
