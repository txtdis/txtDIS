package ph.txtdis.mgdc.service;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ph.txtdis.dto.SalesRevenue;
import ph.txtdis.excel.ExcelReportWriter;
import ph.txtdis.fx.table.AppTable;
import ph.txtdis.service.RestClientService;
import ph.txtdis.service.SyncService;
import ph.txtdis.util.ClientTypeMap;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static java.math.BigDecimal.ZERO;
import static java.util.Arrays.asList;
import static org.apache.log4j.Logger.getLogger;
import static ph.txtdis.util.DateTimeUtils.getServerDate;
import static ph.txtdis.util.DateTimeUtils.toDateDisplay;
import static ph.txtdis.util.UserUtils.username;

@Service("salesRevenueService")
public class SalesRevenueServiceImpl
	implements SalesRevenueService {

	private static Logger logger = getLogger(SalesRevenueServiceImpl.class);

	@Autowired
	protected RestClientService<SalesRevenue> restClientService;

	@Autowired
	private HolidayService holidayService;

	@Autowired
	private SyncService syncService;

	@Autowired
	private ExcelReportWriter excel;

	@Autowired
	private ClientTypeMap typeMap;

	@Value("${prefix.module}")
	private String modulePrefix;

	private LocalDate start, end;

	@Override
	public ClientTypeMap getTypeMap() {
		return typeMap;
	}

	@Override
	public RestClientService<SalesRevenue> getRestClientServiceForLists() {
		return restClientService;
	}

	@Override
	public String getTitleName() {
		return username() + "@" + modulePrefix + " Revenue: " + getSubhead();
	}

	@Override
	public String getSubhead() {
		String d = toDateDisplay(getStartDate());
		if (!start.isEqual(getEndDate()))
			d = d + " - " + toDateDisplay(end);
		return d;
	}

	@Override
	public LocalDate getStartDate() {
		if (start == null)
			start = yesterday();
		return start;
	}

	@Override
	public LocalDate getEndDate() {
		if (end == null)
			end = yesterday();
		return end;
	}

	private LocalDate yesterday() {
		return holidayService.previousWorkDay(getServerDate());
	}

	@Override
	public void setEndDate(LocalDate d) {
		end = d;
	}

	@Override
	public void setStartDate(LocalDate d) {
		start = d;
	}

	@Override
	public List<BigDecimal> getTotals(List<SalesRevenue> l) {
		BigDecimal t = l.stream().map(v -> v.getValue()).reduce(ZERO, BigDecimal::add);
		logger.info("\n    TotalFromSalesRevenue = " + t);
		return asList(t);
	}

	@Override
	public List<SalesRevenue> list() {
		try {
			return restClientService.module(getModuleName())
				.getList("/list?start=" + getStartDate() + "&end=" + getEndDate());
		} catch (Exception e) {
			return null;
		}
	}

	@Override
	public String getModuleName() {
		return "salesRevenue";
	}

	@Override
	public void next() {
		if (getEndDate().isBefore(yesterday()))
			start = end = getEndDate().plusDays(1L);
	}

	@Override
	public void previous() {
		end = start = getStartDate().minusDays(1L);
	}

	@Override
	public void reset() {
		end = null;
		start = null;
	}

	@Override
	@SuppressWarnings("unchecked")
	public void saveAsExcel(AppTable<SalesRevenue>... tables) throws Exception {
		excel.table(tables).filename(excelName()).sheetname(getHeaderName()).write();
	}

	private String excelName() {
		return getHeaderName().replace(" ", ".") + "." +
			getSubhead().replace(" ", "").replace(":", ".").replace("-", ".to.").replace("/", "-");
	}

	@Override
	public String getHeaderName() {
		return "Sales Revenue";
	}
}
