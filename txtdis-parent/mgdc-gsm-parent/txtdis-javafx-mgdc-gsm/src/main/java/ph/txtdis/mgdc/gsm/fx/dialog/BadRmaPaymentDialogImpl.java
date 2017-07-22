package ph.txtdis.mgdc.gsm.fx.dialog;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import ph.txtdis.mgdc.fx.dialog.BadRmaPaymentDialog;
import ph.txtdis.mgdc.gsm.service.BadRefundedRmaService;

@Scope("prototype")
@Component("badRmaPaymentDialog")
public class BadRmaPaymentDialogImpl //
		extends AbstractRmaPaymentDialog<BadRefundedRmaService> //
		implements BadRmaPaymentDialog {
}
