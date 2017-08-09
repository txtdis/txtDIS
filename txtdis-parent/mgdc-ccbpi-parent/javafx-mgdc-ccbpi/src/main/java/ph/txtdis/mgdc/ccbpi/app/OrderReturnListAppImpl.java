package ph.txtdis.mgdc.ccbpi.app;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import ph.txtdis.mgdc.ccbpi.fx.table.OrderReturnListTable;
import ph.txtdis.mgdc.ccbpi.service.OrderReturnListService;

@Scope("prototype")
@Component("orderReturnListApp")
public class OrderReturnListAppImpl // 
	extends AbstractTotaledListApp<OrderReturnListTable, OrderReturnListService> //
	implements OrderReturnListApp {

	@Override
	protected void setList(String[] ids) throws Exception {
		service.listRR(ids);
	}
}
