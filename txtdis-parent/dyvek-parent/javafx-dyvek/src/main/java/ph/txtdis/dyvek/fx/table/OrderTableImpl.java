package ph.txtdis.dyvek.fx.table;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Scope("prototype")
@Component("orderTable")
public class OrderTableImpl //
	extends AbstractBillableDetailTable //
	implements OrderTable {

	@Override
	protected String orderNoColumnName() {
		return "D/R No.";
	}

	@Override
	protected String qtyColumnName() {
		return "Delivered";
	}
}
