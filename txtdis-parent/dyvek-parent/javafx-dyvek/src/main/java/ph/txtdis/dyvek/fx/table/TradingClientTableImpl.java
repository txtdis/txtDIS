package ph.txtdis.dyvek.fx.table;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import ph.txtdis.dyvek.fx.dialog.TradingClientDialog;
import ph.txtdis.dyvek.model.Customer;
import ph.txtdis.fx.table.AbstractNameListTable;

@Scope("prototype")
@Component("tradingClientTable")
public class TradingClientTableImpl //
	extends AbstractNameListTable<Customer, TradingClientDialog> //
	implements TradingClientTable {
}
