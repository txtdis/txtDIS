package ph.txtdis.mgdc.ccbpi.app;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import ph.txtdis.mgdc.ccbpi.fx.table.OrderConfirmationListTable;
import ph.txtdis.mgdc.ccbpi.service.OrderConfirmationListService;

@Scope("prototype")
@Component("orderConfirmationListApp")
public class OrderConfirmationListAppImpl //
		extends AbstractTotaledListApp<OrderConfirmationListTable, OrderConfirmationListService> //
		implements OrderConfirmationListApp {

	@Override
	protected void setList(String[] ids) throws Exception {
		service.listOCS(ids);
	}
}
