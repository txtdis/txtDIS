package ph.txtdis.service;

import java.time.ZonedDateTime;
import java.util.List;

import ph.txtdis.dto.CustomerReceivable;

public interface CustomerReceivableService //
		extends Spreadsheet<CustomerReceivable> {

	static final String AGED_COLUMN = "7";

	static final String AGING_COLUMN = "8";

	String dayCount();

	ZonedDateTime getTimestamp();

	void listInvoicesByCustomerBetweenTwoDayCounts(String... ids) throws Exception;

	List<CustomerReceivable> listReceivables(String... ids) throws Exception;
}