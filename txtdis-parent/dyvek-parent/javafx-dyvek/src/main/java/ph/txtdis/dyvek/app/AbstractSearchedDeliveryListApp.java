package ph.txtdis.dyvek.app;

import ph.txtdis.dyvek.service.ListedAndResetableAndSearchedBillableService;

public abstract class AbstractSearchedDeliveryListApp<S extends ListedAndResetableAndSearchedBillableService> //
	extends AbstractDeliveryListApp<S> {

	@Override
	protected void refreshTable() {
		table.items(service.listSearched());
	}
}
