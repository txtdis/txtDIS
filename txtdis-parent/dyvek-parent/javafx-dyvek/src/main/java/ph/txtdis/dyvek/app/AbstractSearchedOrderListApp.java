package ph.txtdis.dyvek.app;

import ph.txtdis.dyvek.model.Billable;
import ph.txtdis.dyvek.service.OrderService;
import ph.txtdis.fx.table.AppTable;

public abstract class AbstractSearchedOrderListApp<
	OT extends AppTable<Billable>,
	OS extends OrderService>
	extends AbstractListApp<OT, OS, Billable> {

	@Override
	protected void refreshTable() {
		table.items(service.listSearched());
	}
}
