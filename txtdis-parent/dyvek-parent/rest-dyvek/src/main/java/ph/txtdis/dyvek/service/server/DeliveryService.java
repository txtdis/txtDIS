package ph.txtdis.dyvek.service.server;

import java.util.List;

import ph.txtdis.dyvek.domain.BillableEntity;
import ph.txtdis.dyvek.model.Billable;

public interface DeliveryService //
		extends SearchedSpunSavedOrderService<BillableEntity, Billable> {

	Billable findByVendorDeliveryNo(String vendor, String dr);

	List<BillableEntity> findAllByVendorPurchaseNo(Long vendorId, String po);
}
