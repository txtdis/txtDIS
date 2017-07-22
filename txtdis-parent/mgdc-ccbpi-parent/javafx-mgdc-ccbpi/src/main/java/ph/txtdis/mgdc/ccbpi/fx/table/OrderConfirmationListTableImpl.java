package ph.txtdis.mgdc.ccbpi.fx.table;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import ph.txtdis.mgdc.ccbpi.app.OrderConfirmationApp;

@Scope("prototype")
@Component("orderConfirmationListTable")
public class OrderConfirmationListTableImpl extends AbstractOrderListTable<OrderConfirmationApp> implements OrderConfirmationListTable {

	@Override
	protected String qtyAndValuePrefix() {
		return "expected";
	}
}
