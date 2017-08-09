package ph.txtdis.mgdc.gsm.app;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import ph.txtdis.mgdc.app.GoodRmaApp;
import ph.txtdis.mgdc.fx.dialog.GoodRmaPaymentDialog;
import ph.txtdis.mgdc.fx.table.GoodRmaTable;
import ph.txtdis.mgdc.gsm.service.GoodRefundedRmaService;

@Scope("prototype")
@Component("goodRmaApp")
public class GoodRmaAppImpl
	extends AbstractRefundedRmaApp<GoodRefundedRmaService, GoodRmaTable, GoodRmaPaymentDialog> //
	implements GoodRmaApp {

	@Override
	protected String receiptButtonIconName() {
		return "returnReceipt";
	}
}
