package ph.txtdis.mgdc.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ph.txtdis.dto.CreditNote;
import ph.txtdis.dto.CreditNoteDump;
import ph.txtdis.dto.CreditNotePayment;
import ph.txtdis.dto.Keyed;
import ph.txtdis.excel.ExcelReportWriter;
import ph.txtdis.exception.InvalidException;
import ph.txtdis.exception.UnauthorizedUserException;
import ph.txtdis.fx.table.AppTable;
import ph.txtdis.mgdc.fx.table.CreditNoteListTable;
import ph.txtdis.service.RestClientService;
import ph.txtdis.util.ClientTypeMap;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.List;

import static java.math.BigDecimal.ZERO;
import static java.time.LocalDate.now;
import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static java.util.stream.Collectors.toList;
import static ph.txtdis.type.UserType.*;
import static ph.txtdis.util.DateTimeUtils.toDateDisplay;
import static ph.txtdis.util.UserUtils.isUser;
import static ph.txtdis.util.UserUtils.username;

@Service("creditNoteService")
public class CreditNoteServiceImpl //
	implements CreditNoteService {

	private static final String DATA_DUMP = "Data Dump";

	private static final String LIST = "List";

	private static final String UNPAID = "Unpaid";

	private static final String UNVALIDATED = "Unvalidated";

	@Autowired
	private RestClientService<CreditNote> restClientService;

	@Autowired
	private ExcelReportWriter excelWriter;

	@Autowired
	private ClientTypeMap typeMap;

	@Value("${prefix.module}")
	private String modulePrefix;

	private CreditNote creditNote;

	private String excelName, sheetName;

	public CreditNoteServiceImpl() {
		reset();
	}

	@Override
	public void reset() {
		set(null);
		excelName = null;
		sheetName = null;
	}

	@Override
	public <T extends Keyed<Long>> void set(T t) {
		creditNote = (CreditNote) t;
	}

	@Override
	public boolean canReject() {
		return canApprove() || isUser(AUDITOR);
	}

	@Override
	public boolean canApprove() {
		return isUser(MANAGER) || isUser(HEAD_CASHIER);
	}

	@Override
	public void createAndWriteAnExcelDataDumpFile(AppTable<CreditNoteDump> tables) throws IOException {
		setExcelName(getUnpaidHeaderText());
		setSheetName(DATA_DUMP);
		tables.items(getDataDump());
		writeAnExcelFile(tables);
	}

	private void setExcelName(String name) {
		excelName = name;
	}

	@Override
	public String getUnpaidHeaderText() {
		return UNPAID + " " + getHeaderName() + " " + LIST;
	}

	private void setSheetName(String name) {
		sheetName = name;
	}

	@Override
	public List<CreditNoteDump> getDataDump() {
		try {
			return list().stream().flatMap(c -> toCreditNoteDumpList(c).stream()).collect(toList());
		} catch (Exception e) {
			return emptyList();
		}
	}

	private void writeAnExcelFile(AppTable<?>... tables) throws IOException {
		tables[0].setId(excelName());
		excelWriter.table(tables).filename(excelFileName()).sheetname(sheetName).write();
	}

	@Override
	public String getHeaderName() {
		return "Credit Note";
	}

	private List<CreditNoteDump> toCreditNoteDumpList(CreditNote c) {
		if (c.getPayments().isEmpty())
			return asList(creditNoteOnlyDump(c));
		return creditNoteDumpList(c);
	}

	private String excelName() {
		return excelName + " as of " + toDateDisplay(now());
	}

	private String excelFileName() {
		return excelName().replace(" ", ".").replace("/", "-");
	}

	private CreditNoteDump creditNoteOnlyDump(CreditNote c) {
		CreditNoteDump d = new CreditNoteDump();
		d.setId(c.getId());
		d.setCreditDate(c.getCreditDate());
		d.setReference(c.getReference());
		d.setDescription(c.getDescription());
		d.setTotalValue(c.getTotalValue());
		d.setBalanceValue(c.getBalanceValue());
		d.setRemarks(c.getRemarks());
		return d;
	}

	private List<CreditNoteDump> creditNoteDumpList(CreditNote c) {
		return c.getPayments().stream().map(p -> creditNoteWithPaymentDump(c, p)).collect(toList());
	}

	private CreditNoteDump creditNoteWithPaymentDump(CreditNote c, CreditNotePayment p) {
		CreditNoteDump d = creditNoteOnlyDump(c);
		d.setPaymentDate(p.getPaymentDate());
		d.setReference(p.getReference());
		d.setPaymentValue(p.getPaymentValue());
		d.setPaymentRemarks(p.getPaymentRemarks());
		return d;
	}

	@Override
	public CreditNotePayment createPayment(LocalDate d, String reference, BigDecimal payment, String remarks) {
		CreditNotePayment p = new CreditNotePayment();
		p.setPaymentDate(d);
		p.setReference(reference);
		p.setPaymentValue(payment);
		p.setPaymentRemarks(remarks);
		return p;
	}

	@Override
	@SuppressWarnings("unchecked")
	public CreditNote findByModuleId(Long id) throws Exception {
		return findById(id);
	}

	@Override
	public String getAlternateName() {
		return "C/N";
	}

	@Override
	public String getAppendableErrorMessage() {
		return "Head Office Cashiers only";
	}

	@Override
	public BigDecimal getBalance() {
		BigDecimal v = get().getBalanceValue();
		return v != null ? v : ZERO;
	}

	@Override
	public String getCreatedBy() {
		return get().getCreatedBy();
	}

	@Override
	@SuppressWarnings("unchecked")
	public CreditNote get() {
		if (creditNote == null)
			set(new CreditNote());
		return creditNote;
	}

	@Override
	public ZonedDateTime getCreatedOn() {
		return get().getCreatedOn();
	}

	@Override
	public LocalDate getCreditDate() {
		return get().getCreditDate();
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
	public String getDescription() {
		return get().getDescription();
	}

	@Override
	public void setDescription(String text) {
		get().setDescription(text);
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

	@Override
	public String getLastModifiedBy() {
		return get().getLastModifiedBy();
	}

	@Override
	public ZonedDateTime getLastModifiedOn() {
		return get().getLastModifiedOn();
	}

	@Override
	public BigDecimal getPayment() {
		return getTotal().subtract(getBalance());
	}

	@Override
	public List<CreditNotePayment> getPayments() {
		return get().getPayments();
	}

	@Override
	public String getReference() {
		return get().getReference();
	}

	@Override
	public void setReference(String text) {
		get().setReference(text);
	}

	@Override
	public String getRemarks() {
		return get().getRemarks();
	}

	@Override
	public void setRemarks(String text) {
		get().setRemarks(text);
	}

	@Override
	@SuppressWarnings("unchecked")
	public RestClientService<CreditNote> getRestClientService() {
		return restClientService;
	}

	@Override
	@SuppressWarnings("unchecked")
	public RestClientService<CreditNote> getRestClientServiceForLists() {
		return restClientService;
	}

	@Override
	public String getTitleName() {
		return getUsername() + "@" + modulePrefix + " " + CreditNoteService.super.getTitleName();
	}

	@Override
	public String getUsername() {
		return username();
	}

	@Override
	public BigDecimal getTotal() {
		BigDecimal v = get().getTotalValue();
		return v != null ? v : ZERO;
	}

	@Override
	public ClientTypeMap getTypeMap() {
		return typeMap;
	}

	@Override
	public boolean isAppendable() {
		return !isNew() && canApprove();
	}

	@Override
	public List<CreditNote> listUnpaid() {
		return list("/unpaid");
	}

	private List<CreditNote> list(String endPt) {
		try {
			return restClientService.module(getModuleName()).getList(endPt);
		} catch (Exception e) {
			return null;
		}
	}

	@Override
	public String getModuleName() {
		return "creditNote";
	}

	@Override
	public List<CreditNote> listUnvalidated() {
		return list("/unvalidated");
	}

	@Override
	@SuppressWarnings("unchecked")
	public void saveAsExcel(AppTable<CreditNote>... tables) throws IOException {
		setExcelName(getUnpaidHeaderText());
		setSheetName(UNPAID);
		writeAnExcelFile(tables);
	}

	@Override
	public void setCreditDateUponUserValidation(LocalDate d) throws Exception {
		if (!isNew())
			return;
		if (isUser(MANAGER) && isUser(CASHIER))
			throw new UnauthorizedUserException("Branch Office Cashiers only");
		get().setCreditDate(d);
	}

	@Override
	public void updatePayments(List<CreditNotePayment> l) {
		get().setPayments(l);
		get().setBalanceValue(getTotal().subtract(payment()));
	}

	private BigDecimal payment() {
		List<CreditNotePayment> l = get().getPayments();
		return l == null ? ZERO : l.stream().map(CreditNotePayment::getPaymentValue).reduce(ZERO, BigDecimal::add);
	}

	@Override
	public void updateTotals(BigDecimal total) {
		get().setTotalValue(total);
		get().setBalanceValue(total);
	}

	@Override
	public CreditNote validate(Long id) throws Exception {
		CreditNote c = findById(id);
		if (c.getIsValid() == null || !c.getIsValid())
			throw new InvalidException("Only validated credit notes\ncan be used for payments.");
		return c;
	}

	@Override
	public void writeUnpaidExcelFile(CreditNoteListTable table) throws IOException {
		setExcelName(getUnpaidHeaderText());
		setSheetName(UNPAID);
		writeAnExcelFile(table);
	}

	@Override
	public void writeUnvalidatedExcelFile(CreditNoteListTable table) throws IOException {
		setExcelName(getUnvalidatedHeaderText());
		setSheetName(UNVALIDATED);
		writeAnExcelFile(table);
	}

	@Override
	public String getUnvalidatedHeaderText() {
		return UNVALIDATED + " " + getHeaderName() + " " + LIST;
	}
}
