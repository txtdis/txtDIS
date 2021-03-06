package ph.txtdis.dyvek.service.server;

import ph.txtdis.dyvek.domain.BillableEntity;
import ph.txtdis.dyvek.model.Billable;

public interface SalesService //
	extends OpenListedSearchedSpunSavedOrderService<BillableEntity, Billable> {

	Billable findBySalesNo(String client, String salesNo);
}
