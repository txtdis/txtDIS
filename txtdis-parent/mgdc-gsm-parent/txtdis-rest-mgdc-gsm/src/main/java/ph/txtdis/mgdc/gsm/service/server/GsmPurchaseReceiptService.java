package ph.txtdis.mgdc.gsm.service.server;

import ph.txtdis.dto.Billable;

import java.util.List;

public interface GsmPurchaseReceiptService //
	extends PurchaseReceiptService {

	List<Billable> list();

	Billable saveToEdms(Billable t) throws Exception;
}