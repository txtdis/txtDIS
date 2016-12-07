package ph.txtdis.app;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import ph.txtdis.fx.pane.AppGridPane;

@Scope("prototype")
@Component("remittanceApp")
public class PaymentTabledRemittanceAppImpl extends AbstractPaymentTabledRemittanceApp {

	@Override
	protected AppGridPane addGridPane() {
		super.addGridPane();
		depositAndRemarksNodesStartingAtLineNo(3);
		return gridPane;
	}
}
