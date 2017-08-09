package ph.txtdis.dyvek.fx.table;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Scope("prototype")
@Component("agingPayableTable")
public class AgingPayableTableImpl //
	extends AbstractAgingTable //
	implements AgingPayableTable {

	@Override
	protected String customerColumnName() {
		return "Supplier";
	}
}
