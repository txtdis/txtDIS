package ph.txtdis.service;

import static java.math.BigDecimal.ZERO;
import static java.time.LocalDate.now;
import static java.util.Arrays.asList;
import static org.apache.log4j.Logger.getLogger;
import static ph.txtdis.util.DateTimeUtils.toDateDisplay;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import ph.txtdis.dto.Billable;
import ph.txtdis.dto.SalesRevenue;
import ph.txtdis.excel.ExcelWriter;
import ph.txtdis.exception.FailedAuthenticationException;
import ph.txtdis.exception.InvalidException;
import ph.txtdis.exception.NoServerConnectionException;
import ph.txtdis.exception.RestException;
import ph.txtdis.exception.StoppedServerException;
import ph.txtdis.fx.table.AppTable;
import ph.txtdis.util.ClientTypeMap;

@Service("salesRevenueService")
public class SalesRevenueServiceImpl implements SalesRevenueService {

	private static Logger logger = getLogger(SalesRevenueServiceImpl.class);

	@Autowired
	private CredentialService credentialService;

	@Autowired
	private ReadOnlyService<Billable> billableReadOnlyService;

	@Autowired
	protected ReadOnlyService<SalesRevenue> readOnlyService;

	@Autowired
	private ExcelWriter excel;

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
	public ReadOnlyService<Billable> getBillableReadOnlyService() {
		return billableReadOnlyService;
	}

	@Override
	public LocalDate getEndDate() {
		if (end == null)
			end = yesterday();
		return end;
	}

	@Override
	public String getHeaderText() {
		return "Sales Revenue";
	}

	@Override
	public String getModule() {
		return "salesRevenue";
	}

	@Override
	public ReadOnlyService<SalesRevenue> getListedReadOnlyService() {
		return readOnlyService;
	}

	@Override
	public LocalDate getStartDate() {
		if (start == null)
			start = yesterday();
		return start;
	}

	@Override
	public String getSubhead() {
		String d = toDateDisplay(getStartDate());
		if (!start.isEqual(getEndDate()))
			d = d + " - " + toDateDisplay(end);
		return d;
	}

	@Override
	public String getTitleText() {
		return credentialService.username() + "@" + modulePrefix + " Revenue: " + getSubhead();
	}

	@Override
	public List<BigDecimal> getTotals(List<SalesRevenue> l) {
		BigDecimal t = l.stream().map(v -> v.getValue()).reduce(ZERO, BigDecimal::add);
		logger.info("\n    TotalFromSalesRevenue = " + t);
		return asList(t);
	}

	@Override
	public List<SalesRevenue> list() throws NoServerConnectionException, StoppedServerException,
			FailedAuthenticationException, RestException, InvalidException {
		return readOnlyService.module(getModule()).getList("/list?start=" + getStartDate() + "&end=" + getEndDate());
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
	@SuppressWarnings("unchecked")
	public void saveAsExcel(AppTable<SalesRevenue>... tables) throws IOException {
		excel.filename(excelName()).sheetname(getHeaderText()).table(tables).write();
	}

	@Override
	public void setEndDate(LocalDate d) {
		end = d;
	}

	@Override
	public void setStartDate(LocalDate d) {
		start = d;
	}

	private String excelName() {
		return getHeaderText().replace(" ", ".") + "." + getSubhead().replace("-", ".to.").replace("/", "-");
	}

	private LocalDate yesterday() {
		return now().minusDays(1L);
	}
}
