package ph.txtdis.dyvek.app;

import ph.txtdis.dyvek.service.ListedAndResetableAndSearchedBillableService;

public class AbstractAssignedDeliveryListApp<S extends ListedAndResetableAndSearchedBillableService> //
	extends AbstractSearchedDeliveryListApp<S> {

	@Override
	protected String headerAndTitleTextPrefix() {
		return "Assigned";
	}
}
