package ph.txtdis.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import ph.txtdis.dto.DecisionNeededValidatedCreatedKeyed;
import ph.txtdis.dto.Keyed;
import ph.txtdis.dto.Remittance;
import ph.txtdis.excel.ExcelReportWriter;
import ph.txtdis.exception.CashCollectionHasBeenReceivedFromCollectorException;
import ph.txtdis.exception.DuplicateCheckException;
import ph.txtdis.exception.NotFoundException;
import ph.txtdis.fx.table.AppTable;
import ph.txtdis.info.Information;
import ph.txtdis.info.SuccessfulSaveInfo;
import ph.txtdis.type.PaymentType;
import ph.txtdis.util.ClientTypeMap;
import ph.txtdis.util.DateTimeUtils;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.Collections;
import java.util.List;

import static java.util.Collections.singletonList;
import static ph.txtdis.type.PaymentType.*;
import static ph.txtdis.type.UserType.*;
import static ph.txtdis.util.DateTimeUtils.getServerDate;
import static ph.txtdis.util.DateTimeUtils.toHypenatedYearMonthDay;
import static ph.txtdis.util.TextUtils.blankIfNullElseAddCarriageReturn;
import static ph.txtdis.util.UserUtils.isUser;
import static ph.txtdis.util.UserUtils.username;

public abstract class AbstractRemittanceService 
	implements RemittanceService {

	@Autowired
	private FinancialService bankService;

	@Autowired
	private RestClientService<Remittance> restClientService;

	@Autowired
	private UserService userService;

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
	public void reset() {
		set(new Remittance());
		collectors = null;
	}

	@Override
	public <T extends Keyed<Long>> void set(T t) {
		payment = (Remittance) t;
	}

	@Override
	public boolean canApprove() {
		return isAHeadCashier() && getDepositedOn() != null && (isCashPayment() || checkCleared());
	}

	@Override
	public boolean canDepositCash() {
		return isAHeadCashier();
	}

	protected boolean isAHeadCashier() {
		return isUser(HEAD_CASHIER) || isUser(MANAGER);
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
		return isAHeadCashier() || isUser(CASHIER);
	}

	@Override
	public boolean canReceiveTransferredPayments() {
		return isAHeadCashier() 
			&& !isNew() 
			&& getReceivedOn() == null 
			&& getDepositedOn() == null 
			&& getDecidedOn() == null 
			&& (getPaymentDate() != null && getPaymentDate().isAfter(getServerDate()));
	}

	@Override
	public boolean canReject() {
		return isAHeadCashier();
	}

	@Override
	@SuppressWarnings("unchecked")
	public Remittance findByOrderNo(String key) throws Exception {
		Remittance e = findByModuleKey(key);
		if (e == null)
			throw new NotFoundException(getAbbreviatedModuleNoPrompt() + key);
		return e;
	}

	@Override
	public String getAbbreviatedModuleNoPrompt() {
		return getHeaderName() + " ID ";
	}

	@Override
	public String getHeaderName() {
		return "Remittance";
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
	public List<String> getReceivedFromList() {
		if (collectors == null)
			collectors = getCollectors();
		return getId() == null ? collectors : singletonList(get().getReceivedFrom());
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
	@SuppressWarnings("unchecked")
	public Remittance get() {
		if (payment == null)
			reset();
		return payment;
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
	public Long getId() {
		return get().getId();
	}

	@Override
	public void setId(Long id) {
		get().setId(id);
	}

	@Override
	public Boolean getIsValid() {
		return get().getIsValid();
	}

	protected LocalDate goLiveDate() {
		return DateTimeUtils.toDate(goLive);
	}

	@Override
	public List<String> getDraweeBanks() {
		return getId() == null ? listBanks() : singletonList(getDraweeBank());
	}

	protected String getDraweeBank() {
		return get().getDraweeBank();
	}

	@Override
	public void setDraweeBank(String c) {
		if (isNew())
			get().setDraweeBank(c);
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
	public void setPaymentDate(LocalDate d) {
		get().setPaymentDate(d);
	}

	@Override
	public PaymentType[] getPaymentTypes() {
		if (isNew())
			return PaymentType.values();
		return new PaymentType[]{getCheckId() == null ? CASH : CHECK};
	}

	@Override
	public Long getCheckId() {
		return get().getCheckId();
	}

	@Override
	public void setCheckId(Long id) {
		if (isNew())
			get().setCheckId(id);
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
	public void setRemarks(String text) {
		if (getRemarks() != null)
			text = getRemarks() + "\n" + text;
		get().setRemarks(text);
	}

	@Override
	@SuppressWarnings("unchecked")
	public RestClientService<Remittance> getRestClientService() {
		return restClientService;
	}

	@Override
	@SuppressWarnings("unchecked")
	public RestClientService<Remittance> getRestClientServiceForLists() {
		return restClientService;
	}

	@Override
	public String getSubhead() {
		return null;
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

	private RestClientService<Remittance> findRemittance() {
		return restClientService.module(getModuleName());
	}

	@Override
	public String getModuleName() {
		return "remittance";
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

	protected List<Remittance> listRemittances(String endPt) throws Exception {
		return findRemittance().getList(endPt);
	}

	@Override
	public void open(String bank, Long checkId) throws Exception {
		Remittance p = findByCheck(bank, checkId);
		if (p == null)
			throw new NotFoundException("Record of collection\nwith " + bank + " check no. " + checkId);
		set(p);
	}

	@Override
	public Remittance findByCheck(String bank, Long checkId) throws Exception {
		return findRemittance("/check?bank=" + bank + "&id=" + checkId);
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
	@SuppressWarnings("unchecked")
	public void saveAsExcel(AppTable<Remittance>... tables) throws IOException {
		excel.table(tables).filename(excelName()).sheetname(getExcelSheetName()).write();
	}

	private String excelName() {
		return getTitleName().replace(getUsername() + "@", "").replace(" ", ".") + "." + getExcelSheetName();
	}

	private String getExcelSheetName() {
		return toHypenatedYearMonthDay(getServerDate().minusDays(15L)) + ".toDate";
	}

	@Override
	public String getTitleName() {
		return getUsername() + "@" + modulePrefix + " " + RemittanceService.super.getTitleName();
	}

	@Override
	public String getUsername() {
		return username();
	}

	@Override
	public void setCollector(String s) {
		if (isNew())
			get().setReceivedFrom(s);
	}

	@Override
	public void setDepositData(String bank, ZonedDateTime depositedOn) {
		get().setDepositorBank(bank);
		get().setDepositedOn(depositedOn);
		get().setDepositor(getUsername());
		get().setDepositorOn(ZonedDateTime.now());
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
		if (!isNew() || d == null || isUser(MANAGER))
			return;
		validateNoCashPaymentsReceivedFromCollector(d);
	}

	@Override
	public void validateOrderDateBeforeSetting(LocalDate d) throws Exception {
		get().setPaymentDate(d);
	}

	protected boolean checkCleared() {
		return getServerDate().isAfter(getPaymentDate().plusDays(3L));
	}

	@Override
	public <T extends DecisionNeededValidatedCreatedKeyed<Long>> String addDecisionToRemarks(T t,
	                                                                                         Boolean isValid,
	                                                                                         String remarks) {
		String s = blankIfNullElseAddCarriageReturn(t.getRemarks());
		return s + getDecisionTag(isValid, "IN", "VALID", remarks);
	}

	private void validateNoCashPaymentsReceivedFromCollector(LocalDate d) throws Exception {
		String c = get().getReceivedFrom();
		Remittance p = findRemittance("/receivedFrom?name=" + c + "&date=" + d);
		if (p != null)
			throw new CashCollectionHasBeenReceivedFromCollectorException(c, d);
	}
}
