package ph.txtdis.mgdc.ccbpi.app;

import javafx.beans.binding.BooleanBinding;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import ph.txtdis.app.AbstractRemittanceApp;
import ph.txtdis.fx.pane.AppGridPane;
import ph.txtdis.service.RemittanceService;

@Scope("prototype")
@Component("remittanceApp")
public class RemittanceAppImpl //
	extends AbstractRemittanceApp<RemittanceService> {

	@Override
	protected AppGridPane addGridPane() {
		super.addGridPane();
		depositAndRemarksNodesStartingAtLineNo(2);
		return gridPane;
	}

	@Override
	protected BooleanBinding saveButtonDisableBindings() {
		return isPosted() //
			.or(canPostPaymentData.not());
	}
}
