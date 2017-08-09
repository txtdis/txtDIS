package ph.txtdis.mgdc.gsm.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ph.txtdis.dto.Billable;
import ph.txtdis.dto.CreditNote;
import ph.txtdis.dto.Remittance;
import ph.txtdis.dto.RemittanceDetail;
import ph.txtdis.exception.InvalidException;
import ph.txtdis.exception.NotFoundException;
import ph.txtdis.mgdc.service.AdjustableInputtedPaymentDetailedRemittanceService;
import ph.txtdis.mgdc.service.CreditNoteService;
import ph.txtdis.service.AbstractPaymentDetailedRemittanceService;
import ph.txtdis.type.BillingType;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

import static ph.txtdis.type.BillingType.INVOICE;
import static ph.txtdis.type.UserType.MANAGER;
import static ph.txtdis.util.NumberUtils.*;
import static ph.txtdis.util.UserUtils.isUser;

@Service("remittanceService")
public class RemittanceServiceImpl //
	extends AbstractPaymentDetailedRemittanceService //
	implements AdjustableInputtedPaymentDetailedRemittanceService {

	@Autowired
	private BillingService billingService;

	@Autowired
	private CreditNoteService creditNoteService;

	@Value("${limited.fund.account}")
	private String limitedFundAccount;

	@Value("${account.fund.limit}")
	private BigDecimal fundLimit;

	@Value("#{'${adjusting.accounts}'.split(',')}")
	private List<String> adjustingAccounts;

	@Value("#{'${financial.papers}'.split(',')}")
	private List<String> financialPapers;

	private BigDecimal remaining;

	@Override
	public boolean canApprove() {
		if (!isAHeadCashier())
			return false;
		if (financialPapers.contains(getDraweeBank()))
			return true;
		if (getDepositedOn() == null)
			return false;
		return isCashPayment() ? true : checkCleared();
	}

	@Override
	public RemittanceDetail createDetail(Billable billable, BigDecimal payment, BigDecimal remaining) {
		this.remaining = remaining;
		RemittanceDetail d = new RemittanceDetail();
		d.setId(billable.getId());
		d.setOrderNo(billable.getOrderNo());
		d.setCustomer(billable.getCustomerName());
		d.setDueDate(billable.getDueDate());
		d.setTotalDueValue(billable.getTotalValue());
		d.setPaymentValue(payment);
		return addDetail(d);
	}

	private RemittanceDetail addDetail(RemittanceDetail d) {
		List<RemittanceDetail> l = getDetails();
		l.add(d);
		get().setDetails(l);
		return d;
	}

	@Override
	public List<RemittanceDetail> getDetails() {
		List<RemittanceDetail> l = get().getDetails();
		return l == null ? new ArrayList<>() : new ArrayList<>(l);
	}

	@Override
	public Remittance findByBillable(Billable b) {
		try {
			return findRemittance("/billing?id=" + b.getId());
		} catch (Exception e) {
			return null;
		}
	}

	@Override
	public String findWithPendingActions() {
		try {
			Remittance r = findRemittance("/pending");
			return r.getId().toString();
		} catch (Exception e) {
			return null;
		}
	}

	@Override
	public String getPaymentReceivedBy() {
		return get().getReceivedBy();
	}

	@Override
	public ZonedDateTime getPaymentReceivedOn() {
		return get().getReceivedOn();
	}

	@Override
	public BigDecimal getRemaining() {
		return remaining;
	}

	@Override
	public boolean isAppendable() {
		return isPositive(remaining);
	}

	@Override
	public List<String> listBanks() {
		List<String> l = new ArrayList<>(super.listBanks());
		if (!isUser(MANAGER))
			l.removeAll(adjustingAccounts);
		return l;
	}

	@Override
	public void reset() {
		super.reset();
		remaining = null;
	}

	@Override
	public void setPayment(BigDecimal p) {
		super.setPayment(p);
		remaining = p;
	}

	@Override
	public Billable updateUponIdValidation(BillingType type, String prefix, Long id, String suffix) throws Exception {
		id = type.equals(INVOICE) ? id : -id;
		Billable b = billingService.findBilling(prefix, id, suffix);
		String billingNo = billingService.getBillingNoPrompt(prefix, id, suffix);
		if (b == null)
			throw new NotFoundException(billingNo);
		if (isZero(b.getUnpaidValue()))
			throw new Exception(billingNo + "\nis fully paid");
		if (isBillableOnThisPaymentList(b))
			throw new Exception(billingNo + "\nis already on the list");
		if (b.getOrderDate().isEqual(b.getDueDate()) //
			&& isCashPayment() //
			&& b.getDueDate().isAfter(goLiveDate()) //
			&& getPaymentDate().isAfter(goLiveDate()) //
			&& hasNeverBeenInValidated(b))
			validateCashCollection();
		return b;
	}

	@Override
	public boolean isBillableOnThisPaymentList(Billable i) {
		try {
			return getDetails().stream().anyMatch(d -> d.getId().equals(i.getId()));
		} catch (Exception e) {
			return false;
		}
	}

	private boolean hasNeverBeenInValidated(Billable i) {
		try {
			return findRemittance("/invalid?id=" + i.getId()) == null;
		} catch (Exception e) {
			return false;
		}
	}

	@Override
	public List<String> listAdjustingAccounts() {
		return adjustingAccounts;
	}

	@Override
	public void setPaymentReceiptData() {
		get().setReceivedBy(getUsername());
	}

	@Override
	public void validateBankCheckBeforeSetting(String bank) throws Exception {
		if (isLimitedFundExceeded(bank, fundLimit))
			throw new InvalidException(toCurrencyText(fundLimit) + " is the max for\n" + bank);
		if (bank.equalsIgnoreCase("CREDIT MEMO"))
			validateCreditMemo();
		super.validateBankCheckBeforeSetting(bank);
	}

	private boolean isLimitedFundExceeded(String bank, BigDecimal limit) {
		return bank.equalsIgnoreCase(limitedFundAccount) && getValue().compareTo(limit) > 0;
	}

	private void validateCreditMemo() throws Exception {
		CreditNote c = creditNoteService.validate(getCheckId());
		BigDecimal noteValue = c.getTotalValue();
		if (noteValue.compareTo(getValue()) != 0)
			throw new InvalidException("C/N value of " + toCurrencyText(noteValue) + "\ndiffers from payment amount");
	}
}
