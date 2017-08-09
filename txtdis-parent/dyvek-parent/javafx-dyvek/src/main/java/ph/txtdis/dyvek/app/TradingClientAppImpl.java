package ph.txtdis.dyvek.app;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import ph.txtdis.app.AbstractTableApp;
import ph.txtdis.dyvek.fx.table.TradingClientTable;
import ph.txtdis.dyvek.model.Customer;
import ph.txtdis.dyvek.service.TradingClientService;

@Scope("prototype")
@Component("tradingClientApp")
public class TradingClientAppImpl
	extends AbstractTableApp<TradingClientTable, TradingClientService, Customer>
	implements TradingClientApp {
}
