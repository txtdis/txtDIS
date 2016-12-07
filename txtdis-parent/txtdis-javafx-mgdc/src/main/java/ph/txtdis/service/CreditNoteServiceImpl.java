package ph.txtdis.service;

import static java.math.BigDecimal.ZERO;
import static java.time.LocalDate.now;
import static java.util.Arrays.asList;
import static java.util.stream.Collectors.toList;
import static ph.txtdis.util.DateTimeUtils.toDateDisplay;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import ph.txtdis.dto.CreditNote;
import ph.txtdis.dto.CreditNoteDump;
import ph.txtdis.dto.CreditNotePayment;
import ph.txtdis.dto.Keyed;
import ph.txtdis.excel.ExcelWriter;
import ph.txtdis.exception.FailedAuthenticationException;
import ph.txtdis.exception.InvalidException;
import ph.txtdis.exception.NoServerConnectionException;
import ph.txtdis.exception.RestException;
import ph.txtdis.exception.StoppedServerException;
import ph.txtdis.fx.table.AppTable;
import ph.txtdis.util.ClientTypeMap;

@Service("creditNoteService")
public class CreditNoteServiceImpl implements CreditNoteService {

	private static final String LIST = "List";

	private static final String DATA_DUMP = "Data Dump";

	@Autowired
	private CredentialService credentialService;

	@Autowired
	private ReadOnlyService<CreditNote> readOnlyService;

	@Autowired
	private SavingService<CreditNote> savingService;

	@Autowired
	private SpunService<CreditNote, Long> spunService;

	@Autowired
	private ExcelWriter excel;

	@Autowired
	private ClientTypeMap typeMap;

	@Value("${prefix.module}")
	private String modulePrefix;

	private CreditNote creditNote;

	private String excelVersion;

	public CreditNoteServiceImpl() {
		reset();
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
	public CreditNote find(Long id) throws NoServerConnectionException, StoppedServerException,
			FailedAuthenticationException, RestException, InvalidException {
		return readOnlyService.module(getModule()).getOne("/" + id);
	}

	@Override
	@SuppressWarnings("unchecked")
	public CreditNote get() {
		if (creditNote == null)
			reset();
		return creditNote;
	}

	@Override
	public String getAlternateName() {
		return "C/N";
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
	public ZonedDateTime getCreatedOn() {
		return get().getCreatedOn();
	}

	@Override
	public List<CreditNoteDump> getDataDump() {
		try {
			return list().stream().flatMap(c -> toCreditNoteDumpList(c).stream()).collect(toList());
		} catch (Exception e) {
			return Collections.emptyList();
		}
	}

	@Override
	public String getHeaderText() {
		return "Credit Note";
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
	public String getModule() {
		return "creditNote";
	}

	@Override
	public String getPartiallyPaidHeaderText() {
		return getHeaderText() + " " + LIST;
	}

	@Override
	public BigDecimal getPayment() {
		return getTotal().subtract(getBalance());
	}

	@Override
	@SuppressWarnings("unchecked")
	public ReadOnlyService<CreditNote> getReadOnlyService() {
		return readOnlyService;
	}

	@Override
	public ReadOnlyService<CreditNote> getListedReadOnlyService() {
		return readOnlyService;
	}

	@Override
	@SuppressWarnings("unchecked")
	public SavingService<CreditNote> getSavingService() {
		return savingService;
	}

	@Override
	public SpunService<CreditNote, Long> getSpunService() {
		return spunService;
	}

	@Override
	public String getTitleText() {
		return credentialService.username() + "@" + modulePrefix + " " + CreditNoteService.super.getTitleText();
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
	public void reset() {
		set(new CreditNote());
	}

	@Override
	@SuppressWarnings("unchecked")
	public void saveAsExcel(AppTable<CreditNote>... tables) throws IOException {
		tables[0].setId(excelName());
		excel.filename(excelFilename()).sheetname(excelVersion).table(tables).write();
	}

	@Override
	@SuppressWarnings("unchecked")
	public void saveDataDumpAsExcel(AppTable<CreditNoteDump> t) throws IOException {
		t.items(getDataDump());
		saveAsExcelDump(t);
	}

	@SuppressWarnings("unchecked")
	private void saveAsExcelDump(AppTable<CreditNoteDump>... tables) throws IOException {
		tables[0].setId(excelName());
		excel.filename(excelFilename()).sheetname(DATA_DUMP).table(tables).write();
	}

	@Override
	public <T extends Keyed<Long>> void set(T t) {
		creditNote = (CreditNote) t;
	}

	@Override
	public void updatePayments(List<CreditNotePayment> l) {
		get().setPayments(l);
		get().setBalanceValue(getTotal().subtract(payment()));
	}

	@Override
	public void updateTotals(BigDecimal total) {
		get().setTotalValue(total);
		get().setBalanceValue(total);
	}

	private List<CreditNoteDump> creditNoteDumpList(CreditNote c) {
		return c.getPayments().stream().map(p -> creditNoteWithPaymentDump(c, p)).collect(toList());
	}

	private CreditNoteDump creditNoteOnlyDump(CreditNote c) {
		CreditNoteDump d = new CreditNoteDump();
		d.setId(c.getId());
		d.setCreditDate(c.getCreditDate());
		d.setDescription(c.getDescription());
		d.setTotalValue(c.getTotalValue());
		d.setBalanceValue(c.getBalanceValue());
		d.setRemarks(c.getRemarks());
		return d;
	}

	private CreditNoteDump creditNoteWithPaymentDump(CreditNote c, CreditNotePayment p) {
		CreditNoteDump d = creditNoteOnlyDump(c);
		d.setPaymentDate(p.getPaymentDate());
		d.setReference(p.getReference());
		d.setPaymentValue(p.getPaymentValue());
		d.setPaymentRemarks(p.getPaymentRemarks());
		return d;
	}

	private String excelFilename() {
		return excelName().replace(" ", ".").replace("/", "-");
	}

	private String excelName() {
		return getHeaderText() + " " + excelVersion + " as of " + toDateDisplay(now());
	}

	private BigDecimal payment() {
		List<CreditNotePayment> l = get().getPayments();
		return l == null ? ZERO : l.stream().map(CreditNotePayment::getPaymentValue).reduce(ZERO, BigDecimal::add);
	}

	private List<CreditNoteDump> toCreditNoteDumpList(CreditNote c) {
		if (c.getPayments().isEmpty())
			return asList(creditNoteOnlyDump(c));
		return creditNoteDumpList(c);
	}
}
