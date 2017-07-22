package ph.txtdis.mgdc.ccbpi.app;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import ph.txtdis.app.AbstractStockTakeVarianceApp;

@Scope("prototype")
@Component("stockTakeVarianceApp")
public class StockTakeVarianceAppImpl //
		extends AbstractStockTakeVarianceApp {
}
