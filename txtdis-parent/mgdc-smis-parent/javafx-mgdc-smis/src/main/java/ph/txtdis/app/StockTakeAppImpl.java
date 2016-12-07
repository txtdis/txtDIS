package ph.txtdis.app;

import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

@Lazy
@Component("stockTakeApp")
public class StockTakeAppImpl extends AbstractStockTakeApp {
}
