package ph.txtdis.service;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import ph.txtdis.dto.AgingReceivable;
import ph.txtdis.dto.AgingReceivableReport;
import ph.txtdis.excel.ExcelReportWriter;
import ph.txtdis.fx.table.AppTable;
import ph.txtdis.util.ClientTypeMap;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.apache.log4j.Logger.getLogger;
import static ph.txtdis.util.DateTimeUtils.toTimestampFilename;
import static ph.txtdis.util.DateTimeUtils.toTimestampText;
import static ph.txtdis.util.UserUtils.username;

public abstract class AbstractAgingReceivableService
	implements AgingReceivableService {

	private static Logger logger = getLogger(AbstractAgingReceivableService.class);

	@Autowired
	private RestClientService<AgingReceivableReport> restClientService;

	@Autowired
	private ExcelReportWriter excel;

	@Autowired
	private ClientTypeMap typeMap;

	@Value("${prefix.module}")
	private String modulePrefix;

	private AgingReceivableReport report;

	public AbstractAgingReceivableService() {
		reset();
	}

	@Override
	public void reset() {
		report = null;
	}

	@Override
	public String getHeaderName() {
		return "Aging Receivable";
	}

	@Override
	public RestClientService<AgingReceivable> getRestClientServiceForLists() {
		return null;
	}

	@Override
	public String getSubhead() {
		return "A/R as of " + toTimestampText(getTimestamp());
	}

	private ZonedDateTime getTimestamp() {
		logger.info("\n    Timestamp = " + report.getTimestamp());
		logger.info("\n    TimestampInstant = " + report.getTimestamp().withZoneSameInstant(ZoneId.of("Asia/Manila")));
		logger.info("\n    TimestampLocal = " + report.getTimestamp().withZoneSameLocal(ZoneId.of("Asia/Manila")));
		return report.getTimestamp();
	}

	@Override
	public String getTitleName() {
		return username() + "@" + modulePrefix + " Aging A/R";
	}

	@Override
	public List<BigDecimal> getTotals(List<AgingReceivable> l) {
		return report.getTotals();
	}

	@Override
	public ClientTypeMap getTypeMap() {
		return typeMap;
	}

	@Override
	public List<AgingReceivable> list() {
		if (report == null)
			report = generateReport();
		List<AgingReceivable> list = report.getReceivables();
		return list != null ? list : new ArrayList<>();
	}

	private AgingReceivableReport generateReport() {
		try {
			return restClientService.module(getModuleName()).getOne("");
		} catch (Exception e) {
			e.printStackTrace();
			return new AgingReceivableReport();
		}
	}

	@Override
	public String getModuleName() {
		return "agingReceivable";
	}

	@Override
	@SuppressWarnings("unchecked")
	public void saveAsExcel(AppTable<AgingReceivable>... tables) throws IOException {
		excel.table(tables).filename(excelName()).sheetname(getExcelSheetName()).write();
	}

	private String excelName() {
		return "Aging.Receivables." + getExcelSheetName();
	}

	private String getExcelSheetName() {
		return toTimestampFilename(getTimestamp());
	}
}
