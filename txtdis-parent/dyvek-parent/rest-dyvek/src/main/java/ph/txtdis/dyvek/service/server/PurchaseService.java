package ph.txtdis.dyvek.service.server;

import ph.txtdis.dyvek.domain.BillableEntity;
import ph.txtdis.dyvek.model.Billable;

import java.util.List;

public interface PurchaseService //
	extends OpenListedSearchedSpunSavedOrderService<BillableEntity, Billable> {

	List<Billable> findAllOpenEndingInTheNext2Days();

	Billable findByPurchaseNo(String no);
}
