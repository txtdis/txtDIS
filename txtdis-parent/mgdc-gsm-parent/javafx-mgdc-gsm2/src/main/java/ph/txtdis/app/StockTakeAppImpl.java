package ph.txtdis.app;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import ph.txtdis.mgdc.gsm.app.AbstractStockTakeApp;

@Scope("prototype")
@Component("stockTakeApp")
public class StockTakeAppImpl extends AbstractStockTakeApp {
}
