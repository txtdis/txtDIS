package ph.txtdis.service;

import static java.time.DayOfWeek.SUNDAY;
import static java.util.Arrays.asList;
import static ph.txtdis.type.PaymentType.CASH;
import static ph.txtdis.type.PaymentType.CHECK;
import static ph.txtdis.type.PaymentType.values;
import static ph.txtdis.type.UserType.CASHIER;
import static ph.txtdis.type.UserType.COLLECTOR;
import static ph.txtdis.type.UserType.HEAD_CASHIER;
import static ph.txtdis.type.UserType.MANAGER;
import static ph.txtdis.util.DateTimeUtils.toHypenatedYearMonthDay;
import static ph.txtdis.util.TextUtils.blankIfNullElseAddCarriageReturn;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import ph.txtdis.dto.DecisionNeededValidatedCreatedKeyed;
import ph.txtdis.dto.Keyed;
import ph.txtdis.dto.Remittance;
import ph.txtdis.excel.ExcelReportWriter;
import ph.txtdis.exception.CashCollectionHasBeenReceivedFromCollectorException;
import ph.txtdis.exception.DuplicateCheckException;
import ph.txtdis.exception.NotFoundException;
import ph.txtdis.exception.NotTodayOrYesterdayCashPaymentException;
import ph.txtdis.fx.table.AppTable;
import ph.txtdis.info.Information;
import ph.txtdis.info.SuccessfulSaveInfo;
import ph.txtdis.type.PaymentType;
import ph.txtdis.util.ClientTypeMap;
import ph.txtdis.util.DateTimeUtils;

public abstract class AbstractRemittanceService //
		implements RemittanceService {

	@Autowired
	private FinancialService bankService;

	@Autowired
	private CredentialService credentialService;

	@Autowired
	private ReadOnlyService<Remittance> readOnlyService;

	@Autowired
	private SavingService<Remittance> savingService;

	@Autowired
	private SavingService<List<Remittance>> listSavingService;

	@Autowired
	private SpunKeyedService<Remittance, Long> spunService;

	@Autowired
	private UserService userService;

	@Autowired
	protected SyncService syncService;

	@Autowired
	private ClientTypeMap typeMap;

	@Autowired
	private ExcelReportWriter excel;

	@Value("${prefix.module}")
	private String modulePrefix;

	@Value("${go.live}")
	private String goLive;

	private List<String> collectors;

	private Remittance payment;

	public AbstractRemittanceService() {
		reset();
	}

	@Override
	public boolean canApprove() {
		if (!isAHeadCashier())
			return false;
		if (getDepositedOn() == null)
			return false;
		return isCashPayment() ? true : checkCleared();
	}

	protected boolean isAHeadCashier() {
		return credentialService.isUser(HEAD_CASHIER) || credentialService.isUser(MANAGER);
	}

	@Override
	public boolean canDepositCash() {
		return isAHeadCashier();
	}

	@Override
	public boolean canDepositCheck() {
		return isAHeadCashier();
	}

	@Override
	public boolean canPostPaymentData() {
		return isACashier();
	}

	private boolean isACashier() {
		return isAHeadCashier() || credentialService.isUser(CASHIER);
	}

	@Override
	public boolean canReceiveTransferredPayments() {
		return isAHeadCashier() //
				&& !isNew() //
				&& getReceivedOn() == null //
				&& getDepositedOn() == null //
				&& getDecidedOn() == null //
				&& (getPaymentDate() != null && getPaymentDate().isAfter(syncService.getServerDate()));
	}

	@Override
	public boolean canReject() {
		return isAHeadCashier();
	}

	@Override
	public Remittance findByCheck(String bank, Long checkId) throws Exception {
		return findRemittance("/check?bank=" + bank + "&id=" + checkId);
	}

	@Override
	@SuppressWarnings("unchecked")
	public Remittance findByOrderNo(String key) throws Exception {
		Remittance e = (Remittance) findByModuleKey(key);
		if (e == null)
			throw new NotFoundException(getAbbreviatedModuleNoPrompt() + key);
		return e;
	}

	@Override
	@SuppressWarnings("unchecked")
	public Remittance get() {
		if (payment == null)
			reset();
		return payment;
	}

	@Override
	public String getAbbreviatedModuleNoPrompt() {
		return getHeaderName() + " ID ";
	}

	@Override
	public String getAlternateName() {
		return getHeaderName() + " Record";
	}

	@Override
	public List<String> listBanks() {
		try {
			return bankService.listBanks();
		} catch (Exception e) {
			return null;
		}
	}

	@Override
	public Long getCheckId() {
		return get().getCheckId();
	}

	@Override
	public List<String> getReceivedFromList() {
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
	public String getDepositEncodedBy() {
		return get().getDepositor();
	}

	@Override
	public String getDepositedTo() {
		return get().getDepositorBank();
	}

	@Override
	public ZonedDateTime getDepositEncodedOn() {
		return get().getDepositorOn();
	}

	@Override
	public String getHeaderName() {
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
	public ReadOnlyService<Remittance> getListedReadOnlyService() {
		return getReadOnlyService();
	}

	protected LocalDate goLiveDate() {
		return DateTimeUtils.toDate(goLive);
	}

	@Override
	public String getModuleName() {
		return "remittance";
	}

	@Override
	public List<String> getDraweeBanks() {
		return getId() == null ? listBanks() : asList(getDraweeBank());
	}

	protected String getDraweeBank() {
		return get().getDraweeBank();
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
	public String getRemarks() {
		return get().getRemarks();
	}

	@Override
	@SuppressWarnings("unchecked")
	public SavingService<Remittance> getSavingService() {
		return savingService;
	}

	@Override
	public SpunKeyedService<Remittance, Long> getSpunService() {
		return spunService;
	}

	@Override
	public String getSubhead() {
		return null;
	}

	@Override
	public String getTitleName() {
		return getUsername() + "@" + modulePrefix + " " + RemittanceService.super.getTitleName();
	}

	@Override
	public List<BigDecimal> getTotals(List<Remittance> l) {
		return Collections.emptyList();
	}

	@Override
	public ClientTypeMap getTypeMap() {
		return typeMap;
	}

	@Override
	public Remittance getUndepositedPayment(PaymentType t, String seller, LocalDate d) throws Exception {
		return findRemittance("/undeposited?payType=" + t + "&seller=" + seller + "&upTo=" + d);
	}

	protected Remittance findRemittance(String endPt) throws Exception {
		return findRemittance().getOne(endPt);
	}

	@Override
	public BigDecimal getValue() {
		return get().getValue();
	}

	@Override
	public boolean isCashPayment() {
		return getCheckId() == null;
	}

	@Override
	public List<Remittance> list(String bank) {
		try {
			return listRemittances("/checks?bank=" + bank);
		} catch (Exception e) {
			return null;
		}
	}

	@Override
	public void open(String bank, Long checkId) throws Exception {
		Remittance p = findByCheck(bank, checkId);
		if (p == null)
			throw new NotFoundException("Record of collection\nwith " + bank + " check no. " + checkId);
		set(p);
	}

	@Override
	public void reset() {
		set(new Remittance());
		collectors = null;
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
		throw new SuccessfulSaveInfo(get());
	}

	@Override
	public void save(List<Remittance> l) throws Information, Exception {
		listSavingService.module(getModuleName()).save(l);
	}

	@Override
	@SuppressWarnings("unchecked")
	public void saveAsExcel(AppTable<Remittance>... tables) throws IOException {
		excel.table(tables).filename(excelName()).sheetname(getExcelSheetName()).write();
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
	public void setDepositData(String bank, ZonedDateTime depositedOn) {
		get().setDepositorBank(bank);
		get().setDepositedOn(depositedOn);
		get().setDepositor(getUsername());
		get().setDepositorOn(ZonedDateTime.now());
	}

	@Override
	public void setDraweeBank(String c) {
		if (isNew())
			get().setDraweeBank(c);
	}

	@Override
	public void setFundTransferData() {
		get().setReceivedBy(getUsername());
		get().setReceivedOn(ZonedDateTime.now());
	}

	@Override
	public void setPayment(BigDecimal p) {
		get().setValue(p);
	}

	@Override
	public void setPaymentDate(LocalDate d) {
		get().setPaymentDate(d);
	}

	@Override
	public void setRemarks(String text) {
		if (getRemarks() != null)
			text = getRemarks() + "\n" + text;
		get().setRemarks(text);
	}

	@Override
	public void validateBankCheckBeforeSetting(String bank) throws Exception {
		Remittance p = findByCheck(bank, getCheckId());
		if (p != null) {
			setCheckId(null);
			throw new DuplicateCheckException(bank, getCheckId(), p.getId());
		}
		setDraweeBank(bank);
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
		get().setPaymentDate(d);
	}

	protected boolean checkCleared() {
		return syncService.getServerDate().isAfter(getPaymentDate().plusDays(3L));
	}

	private ReadOnlyService<Remittance> findRemittance() {
		return readOnlyService.module(getModuleName());
	}

	protected List<Remittance> listRemittances(String endPt) throws Exception {
		return findRemittance().getList(endPt);
	}

	private String excelName() {
		return getTitleName().replace(getUsername() + "@", "").replace(" ", ".") + "." + getExcelSheetName();
	}

	private String getExcelSheetName() {
		return toHypenatedYearMonthDay(syncService.getServerDate().minusDays(15L)) + ".toDate";
	}

	@Override
	public <T extends DecisionNeededValidatedCreatedKeyed<Long>> String addDecisionToRemarks(T t, Boolean isValid, String remarks) {
		String s = blankIfNullElseAddCarriageReturn(t.getRemarks());
		return s + getDecisionTag(isValid, "IN", "VALID", remarks);
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
	public void setId(Long id) {
		get().setId(id);
	}

	@Override
	public String getUsername() {
		return credentialService.username();
	}
}
