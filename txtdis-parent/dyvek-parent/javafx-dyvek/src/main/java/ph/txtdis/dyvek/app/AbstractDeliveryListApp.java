package ph.txtdis.dyvek.app;

import ph.txtdis.dyvek.fx.table.DeliveryListTable;
import ph.txtdis.dyvek.model.Billable;
import ph.txtdis.service.ListedAndResetableService;

public abstract class AbstractDeliveryListApp<S extends ListedAndResetableService<Billable>> //
		extends AbstractListApp<DeliveryListTable, S, Billable> {

	@Override
	protected String getHeaderText() {
		return headerAndTitleTextPrefix() + " Deliveries";
	}

	@Override
	protected String getTitleText() {
		return headerAndTitleTextPrefix() + " D/R's";
	}

	protected abstract String headerAndTitleTextPrefix();
}
