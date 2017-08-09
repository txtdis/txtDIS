package ph.txtdis.dyvek.fx.table;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Scope("prototype")
@Component("billingTable")
public class BillingTableImpl //
	extends AbstractBillableDetailTable //
	implements BillingTable {

	@Override
	protected String orderNoColumnName() {
		return "D/R No.";
	}

	@Override
	protected String qtyColumnName() {
		return "Quantity";
	}
}
