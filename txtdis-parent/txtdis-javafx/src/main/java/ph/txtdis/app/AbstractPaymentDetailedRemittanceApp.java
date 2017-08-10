package ph.txtdis.app;

import javafx.beans.binding.BooleanBinding;
import javafx.scene.Node;
import javafx.scene.layout.HBox;
import org.springframework.beans.factory.annotation.Autowired;
import ph.txtdis.fx.table.PaymentTable;
import ph.txtdis.service.PaymentDetailedRemittanceService;
import ph.txtdis.service.RemittanceService;

import java.util.ArrayList;
import java.util.List;

public abstract class AbstractPaymentDetailedRemittanceApp<AS extends RemittanceService>
	extends AbstractRemittanceApp<AS> {

	@Autowired
	protected PaymentTable table;

	@Override
	protected void clear() {
		super.clear();
		table.removeListener();
	}

	@Override
	protected List<Node> mainVerticalPaneNodes() {
		List<Node> l = new ArrayList<>(super.mainVerticalPaneNodes());
		l.add(1, table());
		return l;
	}

	private HBox table() {
		return pane.centeredHorizontal(table.build());
	}

	@Override
	protected void refreshInputsAfterPaymentCombo() {
		table.items(((PaymentDetailedRemittanceService) service).getDetails());
		super.refreshInputsAfterPaymentCombo();
	}

	@Override
	protected void setBindings() {
		super.setBindings();
		table.disableIf(paymentCombo.disabledProperty()
			.or(cash().not().and(draweeBankCombo.disabledProperty())));
	}

	@Override
	protected BooleanBinding saveButtonDisableBindings() {
		return isPosted()
			.or(table.isEmpty().and(remarksDisplay.doesNotContain(CANCELLED)))
			.or(canPostPaymentData.not());
	}
}
