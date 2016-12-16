package ph.txtdis.service;

import static java.time.DayOfWeek.SUNDAY;
import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static java.util.stream.Collectors.toList;
import static ph.txtdis.type.PaymentType.CASH;
import static ph.txtdis.type.PaymentType.CHECK;
import static ph.txtdis.type.PaymentType.values;
import static ph.txtdis.type.ScriptType.DEPOSIT;
import static ph.txtdis.type.ScriptType.FUND_TRANSFER;
import static ph.txtdis.type.ScriptType.PAYMENT_VALIDATION;
import static ph.txtdis.type.UserType.CASHIER;
import static ph.txtdis.type.UserType.COLLECTOR;
import static ph.txtdis.type.UserType.HEAD_CASHIER;
import static ph.txtdis.type.UserType.MANAGER;
import static ph.txtdis.util.DateTimeUtils.toDateDisplay;
import static ph.txtdis.util.DateTimeUtils.toHypenatedYearMonthDay;
import static ph.txtdis.util.NumberUtils.isPositive;
import static ph.txtdis.util.NumberUtils.isZero;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import ph.txtdis.dto.Billable;
import ph.txtdis.dto.Customer;
import ph.txtdis.dto.EntityDecisionNeeded;
import ph.txtdis.dto.Keyed;
import ph.txtdis.dto.PaymentHistory;
import ph.txtdis.dto.Remittance;
import ph.txtdis.dto.RemittanceDetail;
import ph.txtdis.excel.ExcelWriter;
import ph.txtdis.exception.CashCollectionHasBeenReceivedFromCollectorException;
import ph.txtdis.exception.DuplicateCheckException;
import ph.txtdis.exception.NotAllowedOffSiteTransactionException;
import ph.txtdis.exception.NotFoundException;
import ph.txtdis.exception.NotTodayOrYesterdayCashPaymentException;
import ph.txtdis.fx.table.AppTable;
import ph.txtdis.info.Information;
import ph.txtdis.info.SuccessfulSaveInfo;
import ph.txtdis.type.BillingType;
import ph.txtdis.type.PaymentType;
import ph.txtdis.type.ScriptType;
import ph.txtdis.util.ClientTypeMap;
import ph.txtdis.util.DateTimeUtils;

@Service("remittanceService")
public class RemittanceServiceImpl implements RemittanceService {

	private static final String COLLECTION = "Record of collection\n";

	@Autowired
	private BillingService billingService;

	@Autowired
	private CredentialService credentialService;

	@Autowired
	private CustomerService customerService;

	@Autowired
	private ReadOnlyService<Remittance> readOnlyService;

	@Autowired
	private ReadOnlyService<PaymentHistory> readOnlyPaymentHistoryService;

	@Autowired
	private SavingService<Remittance> savingService;

	@Autowired
	private SavingService<List<Remittance>> listSavingService;

	@Autowired
	private ScriptService scriptService;

	@Autowired
	private SpunService<Remittance, Long> spunService;

	@Autowired
	private SyncService syncService;

	@Autowired
	private UserService userService;

	@Autowired
	private RestServerService serverService;

	@Autowired
	private ClientTypeMap typeMap;

	@Autowired
	private ExcelWriter excel;

	@Value("${prefix.module}")
	private String modulePrefix;

	@Value("${go.live}")
	private String goLive;

	private BigDecimal remaining;

	private List<Customer> banks;

	private List<String> collectors;

	private Remittance payment;

	public RemittanceServiceImpl() {
		reset();
	}

	@Override
	public boolean canApprove() {
		if (!isUserAHeadCashier())
			return false;
		if (getDepositedOn() == null)
			return false;
		return isCashPayment() ? true : checkCleared();
	}

	private boolean isUserAHeadCashier() {
		return credentialService.isUser(HEAD_CASHIER) || credentialService.isUser(MANAGER);
	}

	@Override
	public RemittanceDetail createDetail(Billable billable, BigDecimal payment, BigDecimal remaining) {
		this.remaining = remaining;
		RemittanceDetail d = new RemittanceDetail();
		d.setId(billable.getId());
		d.setOrderNo(billable.getOrderNo());
		d.setCustomerName(billable.getCustomerName());
		d.setDueDate(billable.getDueDate());
		d.setTotalDueValue(billable.getTotalValue());
		d.setPaymentValue(payment);
		return addDetail(d);
	}

	@Override
	public boolean isBillableOnThisPaymentList(Billable i) {
		try {
			return getDetails().stream().anyMatch(d -> d.getId().equals(i.getId()));
		} catch (Exception e) {
			return false;
		}
	}

	@Override
	@SuppressWarnings("unchecked")
	public Remittance get() {
		if (payment == null)
			reset();
		return payment;
	}

	@Override
	public String getAlternateName() {
		return getHeaderText() + " Record";
	}

	@Override
	public List<String> getBankNames() {
		try {
			return customerService.listBankNames();
		} catch (Exception e) {
			return null;
		}
	}

	@Override
	public List<Customer> getBanks() {
		try {
			return customerService.listBanks();
		} catch (Exception e) {
			return null;
		}
	}

	@Override
	public Long getCheckId() {
		return get().getCheckId();
	}

	@Override
	public List<String> getCollectorNames() {
		if (collectors == null)
			collectors = getCollectors();
		return getId() == null ? collectors : asList(get().getCollector());
	}

	@Override
	public List<String> getCollectors() {
		return userService.listNamesByRole(COLLECTOR);
	}

	@Override
	public String getCreatedBy() {
		return get().getCreatedBy();
	}

	@Override
	public ZonedDateTime getCreatedOn() {
		return get().getCreatedOn();
	}

	@Override
	public String getDecidedBy() {
		return get().getDecidedBy();
	}

	@Override
	public ZonedDateTime getDecidedOn() {
		return get().getDecidedOn();
	}

	@Override
	public ZonedDateTime getDepositedOn() {
		return get().getDepositedOn();
	}

	@Override
	public String getDepositor() {
		return get().getDepositor();
	}

	@Override
	public String getDepositorBank() {
		return get().getDepositorBank();
	}

	@Override
	public ZonedDateTime getDepositorOn() {
		return get().getDepositorOn();
	}

	@Override
	public List<Customer> getDraweeBanks() {
		if (banks == null)
			banks = customerService.listBanks();
		return getId() == null ? banks : asList(toBank(get().getDraweeBank()));
	}

	private Customer toBank(String name) {
		try {
			return customerService.findByName(name);
		} catch (Exception e) {
			return null;
		}
	}

	@Override
	public String getHeaderText() {
		return "Remittance";
	}

	@Override
	public Long getId() {
		return get().getId();
	}

	@Override
	public Boolean getIsValid() {
		return get().getIsValid();
	}

	@Override
	public ReadOnlyService<PaymentHistory> getListedReadOnlyService() {
		return readOnlyPaymentHistoryService;
	}

	protected LocalDate goLiveDate() {
		return DateTimeUtils.toDate(goLive);
	}

	@Override
	public String getModule() {
		return "remittance";
	}

	@Override
	public String getModuleId() {
		return getHeaderText() + " ID ";
	}

	@Override
	public String getOpenDialogHeader() {
		return "Open a Collection Record";
	}

	@Override
	public LocalDate getPaymentDate() {
		return get().getPaymentDate();
	}

	@Override
	public PaymentType[] getPaymentTypes() {
		if (isNew())
			return values();
		return new PaymentType[] { getCheckId() == null ? CASH : CHECK };
	}

	@Override
	@SuppressWarnings("unchecked")
	public ReadOnlyService<Remittance> getReadOnlyService() {
		return readOnlyService;
	}

	@Override
	public String getReceivedBy() {
		return get().getReceivedBy();
	}

	@Override
	public ZonedDateTime getReceivedOn() {
		return get().getReceivedOn();
	}

	@Override
	public BigDecimal getRemaining() {
		return remaining;
	}

	@Override
	public String getRemarks() {
		return get().getRemarks();
	}

	@Override
	@SuppressWarnings("unchecked")
	public SavingService<Remittance> getSavingService() {
		return savingService;
	}

	@Override
	public ScriptService getScriptService() {
		return scriptService;
	}

	@Override
	public <T extends EntityDecisionNeeded<Long>> ScriptType getScriptType(T d) {
		return PAYMENT_VALIDATION;
	}

	@Override
	public Long getSpunId() {
		return isNew() ? null : getId();
	}

	@Override
	public String getSpunModule() {
		return getModule();
	}

	@Override
	public SpunService<Remittance, Long> getSpunService() {
		return spunService;
	}

	@Override
	public String getSubhead() {
		return null;
	}

	@Override
	public String getTitleText() {
		return credentialService.username() + "@" + modulePrefix + " " + RemittanceService.super.getTitleText();
	}

	@Override
	public List<BigDecimal> getTotals(List<PaymentHistory> l) {
		return null;
	}

	@Override
	public ClientTypeMap getTypeMap() {
		return typeMap;
	}

	@Override
	public Remittance getUndepositedPayment(PaymentType t, String seller, LocalDate d) throws Exception {
		return findRemittance("/undeposited?payType=" + t + "&seller=" + seller + "&upTo=" + d);
	}

	private Remittance findRemittance(String endPt) throws Exception {
		return findRemittance().getOne(endPt);
	}

	@Override
	public BigDecimal getValue() {
		return get().getValue();
	}

	@Override
	public boolean isAppendable() {
		return isPositive(remaining);
	}

	@Override
	public boolean isCashPayment() {
		return getCheckId() == null;
	}

	@Override
	public boolean isUserAllowedToMakeCashDeposits() {
		return isUserAHeadCashier();
	}

	@Override
	public boolean isUserAllowedToMakeCheckDeposits() {
		return isUserAHeadCashier();
	}

	@Override
	public boolean isUserAllowedToPostRemittanceData() {
		return isUserACashier();
	}

	private boolean isUserACashier() {
		return isUserAHeadCashier() || credentialService.isUser(CASHIER);
	}

	@Override
	public boolean isUserAllowedToReceiveFundTransfer() {
		return isUserAHeadCashier();
	}

	@Override
	public List<PaymentHistory> list() {
		return listPayments().stream().map(p -> toHistory(p)).collect(toList());
	}

	@Override
	public void nullifyPaymentData(Billable billable) throws Information, Exception {
		List<Remittance> payments = findByBilling(billable);
		if (payments != null)
			save(payments.stream().map(p -> nullifyPaymentData(billable, p)).collect(toList()));
	}

	@Override
	public void open(Customer bank, Long checkId) throws Exception {
		Remittance p = find(bank, checkId);
		if (p == null)
			throw new NotFoundException(COLLECTION + "with " + bank + " check no. " + checkId);
		set(p);
	}

	@Override
	public void reset() {
		set(new Remittance());
		remaining = null;
		collectors = null;
		banks = null;
	}

	@Override
	public void resetInputDataRelatedToPayment() {
		if (!isNew())
			return;
		get().setCheckId(null);
		get().setDraweeBank(null);
		get().setDetails(null);
	}

	@Override
	public void save() throws Information, Exception {
		set(save(get()));
		scriptService.saveScripts();
		throw new SuccessfulSaveInfo(get());
	}

	@Override
	public void save(List<Remittance> l) throws Information, Exception {
		listSavingService.module(getModule()).save(l);
		scriptService.saveScripts();
	}

	@Override
	@SuppressWarnings("unchecked")
	public void saveAsExcel(AppTable<PaymentHistory>... tables) throws IOException {
		excel.filename(excelName()).sheetname(getExcelSheetName()).table(tables).write();
	}

	@Override
	public <T extends Keyed<Long>> void set(T t) {
		payment = (Remittance) t;
	}

	@Override
	public void setCheckId(Long id) {
		if (isNew())
			get().setCheckId(id);
	}

	@Override
	public void setCollector(String s) {
		if (isNew())
			get().setCollector(s);
	}

	@Override
	public void setDepositData(Customer bank, ZonedDateTime depositedOn) {
		get().setDepositorBank(bank.getName());
		get().setDepositedOn(depositedOn);
		get().setDepositor(username());
		get().setDepositorOn(ZonedDateTime.now());
		scriptService.set(DEPOSIT,
				getId() + "|" + bank.getId() + "|" + getDepositedOn() + "|" + getDepositor() + "|" + getDepositorOn());
	}

	@Override
	public void setDraweeBank(String c) {
		if (isNew())
			get().setDraweeBank(c);
	}

	@Override
	public void setFundTransferData() {
		get().setReceivedBy(username());
		get().setReceivedOn(ZonedDateTime.now());
		scriptService.set(FUND_TRANSFER, getId() + "|" + getReceivedBy() + "|" + getReceivedOn());
	}

	@Override
	public void setPayment(BigDecimal p) {
		get().setValue(p);
		remaining = p;
	}

	@Override
	public void setRemarks(String text) {
		if (getRemarks() != null)
			text = getRemarks() + "\n" + text;
		get().setRemarks(text);
	}

	@Override
	public Billable updateUponIdValidation(BillingType b, String prefix, Long id, String suffix) throws Exception {
		id = b.equals(BillingType.INVOICE) ? id : -id;
		Billable i = billingService.getBilling(prefix, id, suffix);
		String orderNo = moduleId(prefix, id, suffix);
		if (i == null)
			throw new NotFoundException(orderNo);
		if (isZero(i.getUnpaidValue()))
			throw new Exception(orderNo + "\nis fully paid");
		if (isBillableOnThisPaymentList(i))
			throw new Exception(orderNo + "\nis already on the list");
		if (i.getOrderDate().isEqual(i.getDueDate()) //
				&& isCashPayment() //
				&& i.getDueDate().isAfter(goLiveDate()) //
				&& getPaymentDate().isAfter(goLiveDate()) //
				&& hasNeverBeenInValidated(i))
			validateCashCollection();
		return i;
	}

	private String moduleId(String prefix, Long id, String suffix) {
		prefix = prefix.isEmpty() ? "" : prefix + "-";
		if (id < 0)
			return BillingType.DELIVERY + " No. " + -id;
		return BillingType.INVOICE + " No. " + prefix + id + suffix;
	}

	private boolean hasNeverBeenInValidated(Billable i) {
		try {
			Remittance r = findRemittance("/invalidated?id=" + i.getId());
			return r == null;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	@Override
	public void validateBankCheckBeforeSetting(Customer bank) throws Exception {
		Remittance p = find(bank, getCheckId());
		if (p != null) {
			setCheckId(null);
			throw new DuplicateCheckException(bank.getName(), getCheckId(), p.getId());
		}
		setDraweeBank(bank.getName());
	}

	@Override
	public void validateCashCollection() throws Exception {
		LocalDate d = getPaymentDate();
		if (!isNew() || d == null || credentialService.isUser(MANAGER))
			return;
		// TODO -- remove later
		// validateCashPaymentDateIsYesterdayOrToday(d);
		validateNoCashPaymentsReceivedFromCollector(d);
	}

	@Override
	public void validateOrderDateBeforeSetting(LocalDate d) throws Exception {
		if (isNew() && isOffSite())
			throw new NotAllowedOffSiteTransactionException();
		get().setPaymentDate(d);
	}

	private RemittanceDetail addDetail(RemittanceDetail d) {
		List<RemittanceDetail> l = getDetails();
		l.add(d);
		get().setDetails(l);
		return d;
	}

	private boolean checkCleared() {
		return syncService.getServerDate().isAfter(getPaymentDate().plusDays(3L));
	}

	private Remittance find(Customer bank, Long checkId) throws Exception {
		return findRemittance("/check?bank=" + bank.getId() + "&id=" + checkId);
	}

	private ReadOnlyService<Remittance> findRemittance() {
		return readOnlyService.module(getModule());
	}

	private List<Remittance> findByBilling(Billable billable) throws Exception {
		return listRemittances("/find?billing=" + billable.getId());
	}

	private List<Remittance> listRemittances(String endPt) throws Exception {
		return findRemittance().getList(endPt);
	}

	@Override
	public List<RemittanceDetail> getDetails() {
		List<RemittanceDetail> l = get().getDetails();
		return l == null ? new ArrayList<>() : new ArrayList<>(l);
	}

	private String excelName() {
		return getTitleText().replace(credentialService.username() + "@", "").replace(" ", ".") + "."
				+ getExcelSheetName();
	}

	private String getExcelSheetName() {
		return toHypenatedYearMonthDay(syncService.getServerDate().minusDays(15L)) + ".toDate";
	}

	private String invalidatedPaymentDueToInvalidInvoicedRemarks(Billable billable) {
		return "[INVALID: " + username() + " - " + toDateDisplay(syncService.getServerDate()) + "] INVALID S/I(D/R) #"
				+ billable.getOrderNo();
	}

	private List<Remittance> listPayments() {
		try {
			return findRemittance().getList();
		} catch (Exception e) {
			e.printStackTrace();
			return emptyList();
		}
	}

	private Remittance nullifyPaymentData(Billable billable, Remittance p) {
		set(p);
		updatePerValidity(false, invalidatedPaymentDueToInvalidInvoicedRemarks(billable));
		return get();
	}

	@Override
	public String getDecisionTag(Boolean isValid) {
		if (isValid == null)
			isValid = false;
		return decisionTag(isValid, "IN", "VALID");
	}

	private PaymentHistory toHistory(Remittance p) {
		PaymentHistory h = new PaymentHistory();
		h.setId(p.getId());
		h.setPaymentDate(p.getPaymentDate());
		h.setDepositBool(p.getDepositedOn() != null);
		h.setValid(p.getIsValid());
		h.setValue(p.getValue());
		return h;
	}

	// TODO -- use later
	@SuppressWarnings("unused")
	private void validateCashPaymentDateIsYesterdayOrToday(LocalDate d) throws Exception {
		LocalDate today = syncService.getServerDate();
		LocalDate yesterday = today.minusDays(1L);
		if (yesterday.getDayOfWeek() == SUNDAY)
			yesterday = today.minusDays(1L);
		if (d.isBefore(yesterday) || d.isAfter(today))
			throw new NotTodayOrYesterdayCashPaymentException();
	}

	private void validateNoCashPaymentsReceivedFromCollector(LocalDate d) throws Exception {
		String c = get().getCollector();
		Remittance p = findRemittance("/collector?name=" + c + "&date=" + d);
		if (p != null)
			throw new CashCollectionHasBeenReceivedFromCollectorException(c, d);
	}

	@Override
	public boolean isOffSite() {
		return serverService.isOffSite();
	}

	@Override
	public void setId(Long id) {
		get().setId(id);
	}

	@Override
	public String username() {
		return credentialService.username();
	}
}
