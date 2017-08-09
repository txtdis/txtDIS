package ph.txtdis.dyvek.app;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import ph.txtdis.dyvek.fx.table.UnbilledListTable;
import ph.txtdis.dyvek.model.Billable;
import ph.txtdis.dyvek.service.ClientBillingService;

@Scope("prototype")
@Component("unbilledDeliveryListApp")
public class UnbilledDeliveryListAppImpl
	extends AbstractListApp<UnbilledListTable, ClientBillingService, Billable>
	implements UnbilledDeliveryListApp {

	private static final String UNBILLED = "Unbilled";

	@Override
	protected void clear() {
		super.clear();
		table.setId(null);
	}

	@Override
	protected String getHeaderText() {
		return UNBILLED + " Deliveries";
	}

	@Override
	protected String getTitleText() {
		return UNBILLED + " D/R's";
	}
}
