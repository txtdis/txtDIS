package ph.txtdis.dyvek.service.server;

import java.util.List;

import ph.txtdis.dyvek.domain.BillableEntity;
import ph.txtdis.dyvek.model.Billable;

public interface PurchaseService //
		extends OpenListedSearchedSpunSavedOrderService<BillableEntity, Billable> {

	List<Billable> findAllOpenEndingInTheNext2Days();

	Billable findByPurchaseNo(String no);
}
