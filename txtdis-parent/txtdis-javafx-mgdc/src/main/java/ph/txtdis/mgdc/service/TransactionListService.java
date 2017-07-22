package ph.txtdis.mgdc.service;

import ph.txtdis.dto.Billable;
import ph.txtdis.service.SavableAsExcelService;

public interface TransactionListService extends SavableAsExcelService<Billable> {

	void listInvoicesByTransactedItem(String[] ids);
}
