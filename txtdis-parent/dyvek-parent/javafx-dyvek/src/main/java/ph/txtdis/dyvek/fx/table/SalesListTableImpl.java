package ph.txtdis.dyvek.fx.table;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Scope("prototype")
@Component("salesListTable")
public class SalesListTableImpl //
	extends AbstractOrderListTable //
	implements SalesListTable {

	@Override
	protected String customerColumnName() {
		return "Customer";
	}

	@Override
	protected String customerGetterName() {
		return "client";
	}

	@Override
	protected String dateColumnName() {
		return "Order";
	}

	@Override
	protected String orderNoColumnName() {
		return "P/O No";
	}

	@Override
	protected String orderNoGetterName() {
		return "salesNo";
	}

	@Override
	protected String qtyColumnName() {
		return "Balance";
	}

	@Override
	protected String qtyGetterName() {
		return "balanceQty";
	}

}
