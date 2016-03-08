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
import ph.txtdis.fx.control.AppButton;
import ph.txtdis.fx.control.AppField;

@Scope("prototype")
@Component("auditDialog")
public class AuditDialog extends InputDialog {

	@Autowired
	private AppField<String> textField;

	@Autowired
	private AppButton acceptButton;

	@Autowired
	private AppButton rejectButton;

	private Boolean isValid;

	@Override
	public void close() {
		textField.clear();
		super.close();
	}

	public AuditDialog disableApprovalButtonIf(ObservableBooleanValue b) {
		acceptButton.disableIf(b);
		return this;
	}

	public AuditDialog disableRejectionButtonIf(ObservableBooleanValue b) {
		rejectButton.disableIf(textField.isEmpty().or(b));
		return this;
	}

	public String getFindings() {
		return textField.getValue();
	}

	public Boolean isValid() {
		return isValid;
	}

	@Override
	public void setFocus() {
		textField.requestFocus();
	}

	private void accept() {
		isValid = true;
		close();
	}

	private Button acceptButton() {
		acceptButton.large("Accept").build();
		acceptButton.setOnAction(event -> accept());
		return acceptButton;
	}

	private void reject() {
		isValid = false;
		close();
	}

	private Button rejectButton() {
		rejectButton.large("Reject").build();
		rejectButton.setOnAction(event -> reject());
		return rejectButton;
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

	@Override
	protected List<Node> nodes() {
		return asList(header(), textField.build(TEXT), buttonBox());
	}

	@Override
	protected void setOnFiredCloseButton() {
		isValid = null;
		super.setOnFiredCloseButton();
	}
}
