package ph.txtdis.mgdc.gsm.service.server;

import ph.txtdis.dto.Billable;
import ph.txtdis.mgdc.gsm.domain.BillableEntity;
import ph.txtdis.mgdc.service.server.SpunSavedReferencedKeyedService;

public interface PurchaseReceiptService //
		extends SpunSavedReferencedKeyedService<BillableEntity, Billable, Long> {
}
