package ph.txtdis.app;

import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import javafx.scene.Node;
import javafx.scene.layout.HBox;
import ph.txtdis.fx.table.PaymentTable;

public abstract class AbstractPaymentTabledRemittanceApp extends AbstractRemittanceApp {

	@Autowired
	private PaymentTable table;

	@Override
	protected List<Node> mainVerticalPaneNodes() {
		return Arrays.asList(addGridPane(), table(), trackedPane());
	}

	private HBox table() {
		return box.forHorizontalPane(table.build());
	}

	@Override
	protected void refreshInputsAfterPaymentCombo() {
		table.items(service.getDetails());
		super.refreshInputsAfterPaymentCombo();
	}

	@Override
	protected void setBindings() {
		super.setBindings();
		table.disableIf(paymentCombo.disabledProperty()//
				.or(cash().not().and(draweeBankCombo.disabledProperty())));
	}

	@Override
	protected void setSaveButtonBindings() {
		saveButton.disableIf(isPosted()//
				.or(table.isEmpty().and(remarksDisplay.isNot("CANCELLED")))//
				.or(userAllowedToPostCollectionData.not()));
	}
}
