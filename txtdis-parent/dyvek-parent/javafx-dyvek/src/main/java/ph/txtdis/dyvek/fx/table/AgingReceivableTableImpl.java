package ph.txtdis.dyvek.fx.table;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Scope("prototype")
@Component("agingReceivableTable")
public class AgingReceivableTableImpl //
		extends AbstractAgingTable //
		implements AgingReceivableTable {

	@Override
	protected String customerColumnName() {
		return "Customer";
	}
}
