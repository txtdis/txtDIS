package ph.txtdis.fx.dialog;

import static java.util.Arrays.asList;
import static ph.txtdis.type.Type.TEXT;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javafx.beans.value.ObservableBooleanValue;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import ph.txtdis.fx.control.AppButtonImpl;
import ph.txtdis.fx.control.AppFieldImpl;

@Scope("prototype")
@Component("auditDialog")
public class AuditDialogImpl //
		extends AbstractInputDialog {

	@Autowired
	private AppFieldImpl<String> textField;

	@Autowired
	private AppButtonImpl acceptButton;

	@Autowired
	private AppButtonImpl rejectButton;

	private Boolean isValid;

	private String findings;

	public AuditDialogImpl disableApprovalButtonIf(ObservableBooleanValue b) {
		acceptButton.disableIf(b);
		return this;
	}

	public AuditDialogImpl disableRejectionButtonIf(ObservableBooleanValue b) {
		rejectButton.disableIf(textField.isEmpty() //
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

	private Button acceptButton() {
		acceptButton.large("Accept").build();
		acceptButton.onAction(event -> accept());
		return acceptButton;
	}

	private void accept() {
		findings = null;
		isValid = true;
		close();
	}

	private Button rejectButton() {
		rejectButton.large("Reject").build();
		rejectButton.onAction(event -> reject());
		return rejectButton;
	}

	private void reject() {
		findings = textField.getValue();
		isValid = false;
		close();
	}

	@Override
	protected HBox buttonBox() {
		HBox b = super.buttonBox();
		b.setAlignment(Pos.CENTER);
		b.setPadding(new Insets(20));
		return b;
	}

	@Override
	protected Button[] buttons() {
		return new Button[] { acceptButton(), rejectButton(), closeButton() };
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
	protected void nullData() {
		super.nullData();
		isValid = null;
		findings = null;
	}
}
