package ph.txtdis.mgdc.gsm.app;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import ph.txtdis.mgdc.app.BadRmaApp;
import ph.txtdis.mgdc.fx.dialog.BadRmaPaymentDialog;
import ph.txtdis.mgdc.fx.table.BadRmaTable;
import ph.txtdis.mgdc.gsm.service.BadRefundedRmaService;

@Scope("prototype")
@Component("badRmaRefundedApp")
public class BadRmaAppImpl //
		extends AbstractRefundedRmaApp<BadRefundedRmaService, BadRmaTable, BadRmaPaymentDialog> //
		implements BadRmaApp {

	@Override
	protected String receiptButtonIconName() {
		return "badOrderReceipt";
	}
}
