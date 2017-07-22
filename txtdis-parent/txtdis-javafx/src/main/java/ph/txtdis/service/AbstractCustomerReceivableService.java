package ph.txtdis.service;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import ph.txtdis.dto.CustomerReceivable;
import ph.txtdis.dto.CustomerReceivableReport;
import ph.txtdis.excel.ExcelReportWriter;
import ph.txtdis.fx.table.AppTable;
import ph.txtdis.util.ClientTypeMap;
import ph.txtdis.util.DateTimeUtils;

public abstract class AbstractCustomerReceivableService //
		implements CustomerReceivableService {

	protected final static int CUSTOMER_ID = 0;

	private final static int COLUMN_INDEX = 1;

	private final static int CURRENT = 2;

	private final static int ONE_TO_SEVEN = 3;

	private final static int EIGHT_TO_FIFTEEN = 4;

	private final static int SIXTEEN_TO_THIRTY = 5;

	private final static int MORE_THAN_THIRTY = 6;

	private final static int AGING = 7;

	@Autowired
	private CredentialService credentialService;

	@Autowired
	private ReadOnlyService<CustomerReceivable> listedReadOnlyService;

	@Autowired
	private ReadOnlyService<CustomerReceivableReport> readOnlyService;

	@Autowired
	private ExcelReportWriter excel;

	@Autowired
	private ClientTypeMap typeMap;

	@Value("${prefix.module}")
	private String modulePrefix;

	private CustomerReceivableReport report;

	protected String customerName;

	private int columnIndex;

	@Override
	public String dayCount() {
		switch (columnIndex) {
			case CURRENT:
				return "Current";
			case ONE_TO_SEVEN:
				return "1-7 Day Overdue";
			case EIGHT_TO_FIFTEEN:
				return "8-15 Day Overdue";
			case SIXTEEN_TO_THIRTY:
				return "15-30 Day Overdue";
			case MORE_THAN_THIRTY:
				return ">30 Day Overdue";
			case AGING:
				return "Aged";
			default:
				return "All";
		}
	}

	@Override
	public String getHeaderName() {
		return "Statement of Account";
	}

	@Override
	public ReadOnlyService<CustomerReceivable> getListedReadOnlyService() {
		return listedReadOnlyService;
	}

	@Override
	public String getModuleName() {
		return "customerReceivable";
	}

	@Override
	public String getSubhead() {
		return customerName + " A/R as of " + DateTimeUtils.toTimestampText(getTimestamp());
	}

	@Override
	public ZonedDateTime getTimestamp() {
		return report == null ? null : report.getTimestamp();
	}

	@Override
	public String getTitleName() {
		return credentialService.username() + "@" + modulePrefix + " " + customerName + " SOA";
	}

	@Override
	public List<BigDecimal> getTotals(List<CustomerReceivable> l) {
		return report == null ? null : report.getTotals();
	}

	@Override
	public ClientTypeMap getTypeMap() {
		return typeMap;
	}

	@Override
	public List<CustomerReceivable> list() {
		return report == null ? new ArrayList<>() : report.getReceivables();
	}

	@Override
	public void listInvoicesByCustomerBetweenTwoDayCounts(String... ids) throws Exception {
		columnIndex = Integer.valueOf(ids[COLUMN_INDEX]);
		report = readOnlyService.module(getModuleName())
				.getOne("?customer=" + ids[CUSTOMER_ID] + "&lowerDayCount=" + lowerDayCount() + "&upperDayCount=" + upperDayCount());
	}

	private int lowerDayCount() {
		switch (columnIndex) {
			case ONE_TO_SEVEN:
			case AGING:
				return 1;
			case EIGHT_TO_FIFTEEN:
				return 8;
			case SIXTEEN_TO_THIRTY:
				return 16;
			case MORE_THAN_THIRTY:
				return 31;
			default:
				return Integer.MIN_VALUE;
		}
	}

	protected int upperDayCount() {
		switch (columnIndex) {
			case CURRENT:
				return 0;
			case ONE_TO_SEVEN:
				return 7;
			case EIGHT_TO_FIFTEEN:
				return 15;
			case SIXTEEN_TO_THIRTY:
				return 30;
			default:
				return Integer.MAX_VALUE;
		}
	}

	@Override
	public List<CustomerReceivable> listReceivables(String... ids) throws Exception {
		listInvoicesByCustomerBetweenTwoDayCounts(ids);
		return list();
	}

	@Override
	public void reset() {
		report = null;
		customerName = null;
		columnIndex = 0;
	}

	@Override
	@SuppressWarnings("unchecked")
	public void saveAsExcel(AppTable<CustomerReceivable>... tables) throws IOException {
		excel.table(tables).filename(excelName()).sheetname(getExcelSheetName()).write();
	}

	private String excelName() {
		return dottedDayCount() + ".Receivables-" + customerFileName() + "." + DateTimeUtils.toTimestampFilename(getTimestamp());
	}

	private String dottedDayCount() {
		return dayCount().replace(" ", ".").replace(">", "More.than");
	}

	private String customerFileName() {
		return customerName.replace(" ", ".") //
		//.replace("(", "-") //
		//.replace(")", "") //
		//.replace("Ñ", "N")
		;
	}

	private String getExcelSheetName() {
		return dayCount().replace(" Day Overdue", "").toUpperCase();
	}
}
