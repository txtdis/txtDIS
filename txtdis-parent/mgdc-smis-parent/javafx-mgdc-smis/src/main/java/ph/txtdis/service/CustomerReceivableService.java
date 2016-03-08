package ph.txtdis.service;

import static ph.txtdis.util.DateTimeUtils.toTimestampFilename;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.text.WordUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ph.txtdis.dto.CustomerReceivable;
import ph.txtdis.dto.CustomerReceivableReport;
import ph.txtdis.excel.ExcelWriter;
import ph.txtdis.excel.Tabular;
import ph.txtdis.exception.FailedAuthenticationException;
import ph.txtdis.exception.InvalidException;
import ph.txtdis.exception.NoServerConnectionException;
import ph.txtdis.exception.NotFoundException;
import ph.txtdis.exception.RestException;
import ph.txtdis.exception.StoppedServerException;
import ph.txtdis.util.DateTimeUtils;
import ph.txtdis.util.TypeMap;

@Service
public class CustomerReceivableService implements Spreadsheet<CustomerReceivable> {

	private final static int CUSTOMER_ID = 0;

	private final static int COLUMN_INDEX = 1;

	private final static int CURRENT = 2;

	private final static int ONE_TO_SEVEN = 3;

	private final static int EIGHT_TO_FIFTEEN = 4;

	private final static int SIXTEEN_TO_THIRTY = 5;

	private final static int MORE_THAN_THIRTY = 6;

	private final static int AGING = 7;

	@Autowired
	private CustomerService customerService;

	@Autowired
	private ExcelWriter excel;

	@Autowired
	private ReadOnlyService<CustomerReceivableReport> readOnlyService;

	@Autowired
	private TypeMap typeMap;

	private CustomerReceivableReport report;

	private String customerName;

	private int columnIndex;

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

	public String getCustomerName() {
		return customerName == null ? "" : WordUtils.capitalizeFully(customerName);
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
	public ReadOnlyService<CustomerReceivable> getReadOnlyService() {
		return null;
	}

	@Override
	public String getSubhead() {
		return getCustomerName() + " A/R as of " + DateTimeUtils.toTimestampText(getTimestamp());
	}

	public ZonedDateTime getTimestamp() {
		return report == null ? null : report.getTimestamp();
	}

	@Override
	public String getTitleText() {
		return getCustomerName() + " SOA";
	}

	@Override
	public List<BigDecimal> getTotals() {
		return report == null ? null : report.getTotals();
	}

	@Override
	public TypeMap getTypeMap() {
		return typeMap;
	}

	@Override
	public List<CustomerReceivable> list() {
		return report == null ? new ArrayList<>() : report.getReceivables();
	}

	public void listInvoicesByCustomerBetweenTwoDayCounts(String... ids) throws NoServerConnectionException,
			StoppedServerException, FailedAuthenticationException, RestException, InvalidException, NotFoundException {
		columnIndex = Integer.valueOf(ids[COLUMN_INDEX]);
		report = readOnlyService.module(getModule()).getOne(//
				"?customer=" + ids[CUSTOMER_ID] + "&lowerDayCount=" + lowerDayCount() + //
						"&upperDayCount=" + upperDayCount());
		customerName = customerService.find(ids[CUSTOMER_ID]).getName();
	}

	public List<CustomerReceivable> listReceivables(String... ids) throws NoServerConnectionException,
			StoppedServerException, FailedAuthenticationException, RestException, InvalidException, NotFoundException {
		listInvoicesByCustomerBetweenTwoDayCounts(ids);
		return list();
	}

	@Override
	public void saveAsExcel(Tabular... tables) throws IOException {
		excel.filename(getExcelFileName()).sheetname(getExcelSheetName()).table(tables).write();
	}

	private String dottedCustomerName() {
		return getCustomerName().replace(" ", ".") + ".";
	}

	private String dottedDayCount() {
		return dayCount().replace(" ", ".");
	}

	private String getExcelFileName() {
		return dottedDayCount() + ".Receivables-" + dottedCustomerName() + toTimestampFilename(getTimestamp());
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

	private int upperDayCount() {
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
