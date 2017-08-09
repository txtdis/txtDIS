package ph.txtdis.fx.dialog;

import javafx.beans.value.ObservableBooleanValue;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.layout.HBox;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import ph.txtdis.fx.control.AppButton;
import ph.txtdis.fx.control.AppFieldImpl;

import java.util.List;

import static java.util.Arrays.asList;
import static ph.txtdis.type.Type.TEXT;

@Scope("prototype")
@Component("auditDialog")
public class AuditDialog
	extends AbstractInputDialog {

	@Autowired
	private AppFieldImpl<String> textField;

	private AppButton acceptButton, rejectButton;

	private Boolean isValid;

	private String findings;

	public AuditDialog disableApprovalButtonIf(ObservableBooleanValue b) {
		acceptButton.disableIf(b);
		return this;
	}

	public AuditDialog disableRejectionButtonIf(ObservableBooleanValue b) {
		rejectButton.disableIf(textField.isEmpty()
			.or(b));
		return this;
	}

	public String getFindings() {
		return findings;
	}

	@Override
	public void goToDefaultFocus() {
		textField.clear();
		textField.requestFocus();
	}

	@Override
	protected List<AppButton> buttons() {
		return asList(acceptButton(), rejectButton(), closeButton());
	}

	private AppButton acceptButton() {
		acceptButton = button.large("Accept").build();
		acceptButton.onAction(event -> accept());
		return acceptButton;
	}

	private AppButton rejectButton() {
		rejectButton = button.large("Reject").build();
		rejectButton.onAction(event -> reject());
		return rejectButton;
	}

	private void accept() {
		findings = null;
		isValid = true;
		close();
	}

	private void reject() {
		findings = textField.getValue();
		isValid = false;
		close();
	}

	@Override
	protected String headerText() {
		return "Enter Remarks";
	}

	public Boolean isValid() {
		return isValid;
	}

	@Override
	protected List<Node> nodes() {
		return asList(header(), textField.build(TEXT), buttonBox());
	}

	@Override
	protected HBox buttonBox() {
		HBox b = super.buttonBox();
		b.setAlignment(Pos.CENTER);
		b.setPadding(new Insets(20));
		return b;
	}

	@Override
	protected void nullData() {
		super.nullData();
		isValid = null;
		findings = null;
	}
}
