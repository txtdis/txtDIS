package ph.txtdis.mgdc.gsm.fx.dialog;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import ph.txtdis.mgdc.fx.dialog.GoodRmaPaymentDialog;
import ph.txtdis.mgdc.gsm.service.GoodRefundedRmaService;

@Scope("prototype")
@Component("goodRmaPaymentDialog")
public class GoodRmaPaymentDialogImpl //
		extends AbstractRmaPaymentDialog<GoodRefundedRmaService> //
		implements GoodRmaPaymentDialog {
}
