package ph.txtdis.dyvek.service.server;

import ph.txtdis.dyvek.domain.BillableEntity;
import ph.txtdis.dyvek.model.Billable;

public interface ClientBillService //
	extends OpenListedSearchedSpunSavedOrderService<BillableEntity, Billable> {

	Billable findByBillId(String no);

	Billable findUnbilledById(Long id);
}
