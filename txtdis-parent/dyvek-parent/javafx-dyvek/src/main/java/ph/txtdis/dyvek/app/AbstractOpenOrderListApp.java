package ph.txtdis.dyvek.app;

import ph.txtdis.dyvek.model.Billable;
import ph.txtdis.dyvek.service.OrderService;
import ph.txtdis.fx.table.AppTable;

public abstract class AbstractOpenOrderListApp< //
		OT extends AppTable<Billable>, //
		OS extends OrderService> //
		extends AbstractListApp<OT, OS, Billable> {

	private static final String OPEN = "Open ";

	@Override
	protected String getHeaderText() {
		return OPEN + super.getHeaderText();
	}

	@Override
	protected String getTitleText() {
		return OPEN + super.getTitleText();
	}
}
