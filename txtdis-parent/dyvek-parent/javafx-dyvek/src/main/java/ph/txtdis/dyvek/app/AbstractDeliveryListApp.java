package ph.txtdis.dyvek.app;

import ph.txtdis.dyvek.fx.table.DeliveryListTable;
import ph.txtdis.dyvek.model.Billable;
import ph.txtdis.service.ListedAndResettableService;

public abstract class AbstractDeliveryListApp<S extends ListedAndResettableService<Billable>> //
	extends AbstractListApp<DeliveryListTable, S, Billable> {

	@Override
	protected String getHeaderText() {
		return headerAndTitleTextPrefix() + " Deliveries";
	}

	protected abstract String headerAndTitleTextPrefix();

	@Override
	protected String getTitleText() {
		return headerAndTitleTextPrefix() + " D/R's";
	}
}
