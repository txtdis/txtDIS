package ph.txtdis.mgdc.gsm.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ph.txtdis.excel.ExcelReportWriter;
import ph.txtdis.fx.table.AppTable;
import ph.txtdis.mgdc.gsm.dto.Vat;
import ph.txtdis.service.RestClientService;
import ph.txtdis.util.ClientTypeMap;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static java.math.BigDecimal.ONE;
import static java.math.BigDecimal.ZERO;
import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static ph.txtdis.util.DateTimeUtils.*;
import static ph.txtdis.util.NumberUtils.divide;
import static ph.txtdis.util.UserUtils.username;

@Service("vatService")
public class VatServiceImpl
	implements VatService {

	@Autowired
	public ClientTypeMap typeMap;

	@Autowired
	private RestClientService<Vat> restClientService;

	@Autowired
	private ExcelReportWriter excel;

	@Value("${prefix.module}")
	private String modulePrefix;

	private BigDecimal vatRate;

	private LocalDate start, end;

	@Override
	public LocalDate getEndDate() {
		if (end == null)
			setEndDate(getStartDate());
		return end;
	}

	@Override
	public void setEndDate(LocalDate d) {
		end = endOfMonth(d);
	}

	@Override
	public String getHeaderName() {
		return "Value-Added Tax";
	}

	@Override
	public RestClientService<Vat> getRestClientServiceForLists() {
		return restClientService;
	}

	@Override
	public String getSubhead() {
		return toFullMonthYear(getStartDate());
	}

	@Override
	public LocalDate getStartDate() {
		if (start == null)
			setStartDate(getServerDate());
		return start;
	}

	@Override
	public void setStartDate(LocalDate d) {
		start = startOfMonth(d);
		setEndDate(d);
	}

	@Override
	public String getTitleName() {
		return username() + "@" + modulePrefix + " " + getAllCapModule() + " " + toLongMonthYear(getStartDate());
	}

	private String getAllCapModule() {
		return getModuleName().toUpperCase();
	}

	@Override
	public String getModuleName() {
		return "vat";
	}

	@Override
	public List<BigDecimal> getTotals(List<Vat> l) {
		return asList(getTotalInvoiceValue(l), getTotalVatableValue(l), getTotalVatValue(l));
	}

	private BigDecimal getTotalInvoiceValue(List<Vat> l) {
		return l.stream().map(v -> v.getValue()).reduce(ZERO, BigDecimal::add);
	}

	private BigDecimal getTotalVatableValue(List<Vat> l) {
		return l.stream().map(v -> v.getVatableValue()).reduce(ZERO, BigDecimal::add);
	}

	private BigDecimal getTotalVatValue(List<Vat> l) {
		return l.stream().map(v -> v.getVatValue()).reduce(ZERO, BigDecimal::add);
	}

	@Override
	public ClientTypeMap getTypeMap() {
		return typeMap;
	}

	@Override
	public BigDecimal getVat(BigDecimal total) {
		return total.multiply(getVatRate());
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
			return findVat().getOne("/rate");
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	private RestClientService<Vat> findVat() {
		return restClientService.module(getModuleName());
	}

	@Override
	public BigDecimal getVatable(BigDecimal total) {
		return divide(total, getVatDivisor());
	}

	private BigDecimal getVatDivisor() {
		return ONE.add(getVatRate());
	}

	@Override
	public List<Vat> list() {
		try {
			return findVat().getList("/list?start=" + getStartDate() + "&end=" + getEndDate());
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
	public void reset() {
		vatRate = null;
		start = null;
		end = null;
	}

	@Override
	@SuppressWarnings("unchecked")
	public void saveAsExcel(AppTable<Vat>... tables) throws IOException {
		excel.table(tables).filename(excelName()).sheetname(getExcelSheetName()).write();
	}

	private String excelName() {
		return getAllCapModule() + "." + toDottedYearMonth(getStartDate());
	}

	private String getExcelSheetName() {
		return toLongMonthYear(getStartDate());
	}
}
