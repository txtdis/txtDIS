package ph.txtdis.mgdc.ccbpi.fx.table;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import ph.txtdis.mgdc.ccbpi.app.OrderReturnApp;

@Scope("prototype")
@Component("orderReturnListTable")
public class OrderReturnListTableImpl
	extends AbstractOrderListTable<OrderReturnApp>
	implements OrderReturnListTable {

	@Override
	protected String qtyAndValuePrefix() {
		return "returned";
	}
}
