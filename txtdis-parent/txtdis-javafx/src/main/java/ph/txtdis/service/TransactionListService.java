package ph.txtdis.service;

import ph.txtdis.dto.Billable;

public interface TransactionListService extends Excel<Billable> {

	void listInvoicesByTransactedItem(String[] ids);
}
