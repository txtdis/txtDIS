package ph.txtdis.dyvek.service;

import ph.txtdis.dyvek.fx.table.AssignmentTable;
import ph.txtdis.excel.ExcelBillService;

public interface ClientBillingService //
		extends BillingService, ExcelBillService {

	void generateBill(AssignmentTable table) throws Exception;

	String getSalesNo();
}
