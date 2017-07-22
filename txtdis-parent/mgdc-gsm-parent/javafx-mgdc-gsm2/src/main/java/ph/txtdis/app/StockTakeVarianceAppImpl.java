package ph.txtdis.app;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Scope("prototype")
@Component("stockTakeVarianceApp")
public class StockTakeVarianceAppImpl extends AbstractStockTakeVarianceApp {
}
