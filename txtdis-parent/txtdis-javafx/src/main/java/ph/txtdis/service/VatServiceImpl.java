package ph.txtdis.service;

import static java.math.BigDecimal.ONE;
import static java.math.BigDecimal.ZERO;
import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static ph.txtdis.util.DateTimeUtils.endOfMonth;
import static ph.txtdis.util.DateTimeUtils.startOfMonth;
import static ph.txtdis.util.DateTimeUtils.toDottedYearMonth;
import static ph.txtdis.util.DateTimeUtils.toFullMonthYear;
import static ph.txtdis.util.DateTimeUtils.toLongMonthYear;
import static ph.txtdis.util.NumberUtils.divide;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import ph.txtdis.dto.Vat;
import ph.txtdis.excel.ExcelWriter;
import ph.txtdis.fx.table.AppTable;
import ph.txtdis.util.ClientTypeMap;

@Service("vatService")
public class VatServiceImpl implements VatService {

	@Autowired
	private CredentialService credentialService;

	@Autowired
	private ReadOnlyService<Vat> readOnlyService;

	@Autowired
	protected SyncService syncService;

	@Autowired
	private ExcelWriter excel;

	@Autowired
	public ClientTypeMap typeMap;

	@Value("${prefix.module}")
	private String modulePrefix;

	@Override
	public ClientTypeMap getTypeMap() {
		return typeMap;
	}

	private BigDecimal vatRate;

	private LocalDate start, end;

	@Override
	public String getHeaderText() {
		return "Value-Added Tax";
	}

	@Override
	public String getModule() {
		return "vat";
	}

	@Override
	public ReadOnlyService<Vat> getListedReadOnlyService() {
		return readOnlyService;
	}

	@Override
	public String getSubhead() {
		return toFullMonthYear(getStartDate());
	}

	@Override
	public String getTitleText() {
		return credentialService.username() + "@" + modulePrefix + " " + getAllCapModule() + " "
				+ toLongMonthYear(getStartDate());
	}

	@Override
	public List<BigDecimal> getTotals(List<Vat> l) {
		return asList(getTotalInvoiceValue(l), getTotalVatValue(l));
	}

	@Override
	public List<Vat> list() {
		try {
			return readOnlyService.module(getModule()).getList("/list?start=" + getStartDate() + "&end=" + getEndDate());
		} catch (Exception e) {
			e.printStackTrace();
			return emptyList();
		}
	}

	@Override
	public void next() {
		setStartDate(getStartDate().plusMonths(1L));
		setEndDate(getStartDate());
	}

	@Override
	public void previous() {
		setStartDate(getStartDate().minusMonths(1L));
		setEndDate(getStartDate());
	}

	@Override
	@SuppressWarnings("unchecked")
	public void saveAsExcel(AppTable<Vat>... tables) throws IOException {
		excel.filename(excelName()).sheetname(getExcelSheetName()).table(tables).write();
	}

	@Override
	public BigDecimal getVat(BigDecimal total) {
		return total.multiply(getVatRate());
	}

	@Override
	public BigDecimal getVatable(BigDecimal total) {
		return divide(total, getVatDivisor());
	}

	private BigDecimal getVatDivisor() {
		return ONE.add(getVatRate());
	}

	@Override
	public BigDecimal getVatRate() {
		return vatRate == null ? computeVat() : vatRate;
	}

	private BigDecimal computeVat() {
		Vat v = getVat();
		BigDecimal vat = v.getVatValue();
		return vatRate = divide(vat, (v.getValue().subtract(vat)));
	}

	private Vat getVat() {
		try {
			return readOnlyService.module(getModule()).getOne("/rate");
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	private String getAllCapModule() {
		return "VAT";
	}

	private String excelName() {
		return getAllCapModule() + "." + toDottedYearMonth(getStartDate());
	}

	private String getExcelSheetName() {
		return toLongMonthYear(getStartDate());
	}

	private BigDecimal getTotalInvoiceValue(List<Vat> l) {
		return l.stream().map(v -> v.getValue()).reduce(ZERO, BigDecimal::add);
	}

	private BigDecimal getTotalVatValue(List<Vat> l) {
		return l.stream().map(v -> v.getVatValue()).reduce(ZERO, BigDecimal::add);
	}

	@Override
	public LocalDate getEndDate() {
		if (end == null)
			setEndDate(syncService.getServerDate());
		return end;
	}

	@Override
	public LocalDate getStartDate() {
		if (start == null)
			setStartDate(syncService.getServerDate());
		return start;
	}

	@Override
	public void setEndDate(LocalDate d) {
		end = endOfMonth(d);
	}

	@Override
	public void setStartDate(LocalDate d) {
		start = startOfMonth(d);
	}
}
