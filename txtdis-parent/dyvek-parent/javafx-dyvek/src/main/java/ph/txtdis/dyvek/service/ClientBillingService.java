package ph.txtdis.dyvek.service;

import ph.txtdis.excel.ExcelBillService;

public interface ClientBillingService //
	extends BillingService,
	ExcelBillService {

	void generateBill() throws Exception;

	String getSalesNo();
}
