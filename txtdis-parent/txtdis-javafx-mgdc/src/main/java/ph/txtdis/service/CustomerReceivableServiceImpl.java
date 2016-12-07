package ph.txtdis.service;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import ph.txtdis.dto.Customer;
import ph.txtdis.dto.CustomerReceivable;
import ph.txtdis.dto.CustomerReceivableReport;
import ph.txtdis.excel.ExcelWriter;
import ph.txtdis.fx.table.AppTable;
import ph.txtdis.util.ClientTypeMap;
import ph.txtdis.util.DateTimeUtils;

@Service("customerReceivableService")
public class CustomerReceivableServiceImpl implements CustomerReceivableService {

	private final static int CUSTOMER_ID = 0;

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
	private CustomerService customerService;

	@Autowired
	private ReadOnlyService<CustomerReceivable> listedReadOnlyService;

	@Autowired
	private ReadOnlyService<CustomerReceivableReport> readOnlyService;

	@Autowired
	private ExcelWriter excel;

	@Autowired
	private ClientTypeMap typeMap;

	@Value("${prefix.module}")
	private String modulePrefix;

	private CustomerReceivableReport report;

	private String customerName;

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
	public String getHeaderText() {
		return "Statement of Account";
	}

	@Override
	public String getModule() {
		return "customerReceivable";
	}

	@Override
	public ReadOnlyService<CustomerReceivable> getListedReadOnlyService() {
		return listedReadOnlyService;
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
	public String getTitleText() {
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
		report = readOnlyService.module(getModule()).getOne("?customer=" + ids[CUSTOMER_ID] + "&lowerDayCount="
				+ lowerDayCount() + "&upperDayCount=" + upperDayCount());
		customerName = ((Customer) customerService.findById("/" + ids[CUSTOMER_ID])).getName();
	}

	@Override
	public List<CustomerReceivable> listReceivables(String... ids) throws Exception {
		listInvoicesByCustomerBetweenTwoDayCounts(ids);
		return list();
	}

	@Override
	@SuppressWarnings("unchecked")
	public void saveAsExcel(AppTable<CustomerReceivable>... tables) throws IOException {
		excel.filename(excelName()).sheetname(getExcelSheetName()).table(tables).write();
	}

	private String excelName() {
		return dottedDayCount() + ".Receivables-" + customerFileName() + "."
				+ DateTimeUtils.toTimestampFilename(getTimestamp());
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
}
