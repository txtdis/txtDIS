package ph.txtdis.mgdc.gsm.service.server;

import java.util.List;

import ph.txtdis.dto.Billable;

public interface GsmPurchaseReceiptService //
		extends PurchaseReceiptService {

	List<Billable> list();

	Billable saveToEdms(Billable t) throws Exception;
}