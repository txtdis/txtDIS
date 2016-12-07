package ph.txtdis.app;

import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

@Lazy
@Component("stockTakeVarianceApp")
public class StockTakeVarianceAppImpl extends AbstractStockTakeVarianceApp {

	@Override
	public void start() {
		super.start();
		close();
		dialog.showInfo("Please use GSM2 App").addParent(this).start();
	}
}
