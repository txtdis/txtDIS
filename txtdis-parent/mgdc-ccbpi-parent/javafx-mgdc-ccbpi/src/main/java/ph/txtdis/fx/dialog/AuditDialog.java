package ph.txtdis.fx.dialog;

import static java.time.LocalDate.now;
import static java.util.Arrays.asList;
import static ph.txtdis.type.Type.TEXT;
import static ph.txtdis.util.SpringUtil.username;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import static ph.txtdis.util.DateTimeUtils.toDateDisplay;

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

	private String findings;

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
		rejectButton.disableIf(b);
		return this;
	}

	public String findings() {
		return findings;
	}

	public String getFindings() {
		return findings;
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
		findings = tag("VALID");
		close();
	}

	private Button acceptButton() {
		acceptButton.large("Accept").build();
		acceptButton.setOnAction(event -> accept());
		return acceptButton;
	}

	private void reject() {
		isValid = false;
		findings = tag("INVALID");
		close();
	}

	private Button rejectButton() {
		rejectButton.large("Reject").build();
		rejectButton.setOnAction(event -> reject());
		rejectButton.disableIf(textField.isEmpty());
		return rejectButton;
	}

	private String tag(String s) {
		return "[" + s + ": " + username() + " - " + toDateDisplay(now()) + "] " + textField.getText();
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
