package ph.txtdis.dyvek.service.server;

import ph.txtdis.dyvek.domain.BillableEntity;
import ph.txtdis.dyvek.model.Billable;

import java.util.List;

public interface DeliveryService //
	extends SearchedSpunSavedOrderService<BillableEntity, Billable> {

	Billable findByVendorDeliveryNo(String vendor, String dr);

	List<BillableEntity> findAllByVendorPurchaseNo(Long vendorId, String po);
}
