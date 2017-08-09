package ph.txtdis.mgdc.ccbpi.app;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import ph.txtdis.mgdc.ccbpi.fx.table.DeliveryListListTable;
import ph.txtdis.mgdc.ccbpi.service.DeliveryListListService;

@Scope("prototype")
@Component("deliveryListListApp")
public class DeliveryListListAppImpl //
	extends AbstractTotaledListApp<DeliveryListListTable, DeliveryListListService> //
	implements DeliveryListListApp {

	@Override
	protected void setList(String[] ids) throws Exception {
		service.listDDL(ids);
	}
}
