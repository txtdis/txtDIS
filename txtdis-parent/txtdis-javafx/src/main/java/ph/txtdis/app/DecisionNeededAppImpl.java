package ph.txtdis.app;

import static java.util.Arrays.asList;
import static ph.txtdis.type.Type.TEXT;
import static ph.txtdis.type.Type.TIMESTAMP;

import java.time.ZonedDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javafx.beans.binding.BooleanBinding;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ObservableBooleanValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.stage.Stage;
import ph.txtdis.fx.control.AppButtonImpl;
import ph.txtdis.fx.control.AppFieldImpl;
import ph.txtdis.fx.control.LabelFactory;
import ph.txtdis.fx.dialog.AuditDialogImpl;
import ph.txtdis.fx.dialog.MessageDialog;
import ph.txtdis.info.Information;
import ph.txtdis.service.DecisionNeededService;
import ph.txtdis.service.SavedService;
import ph.txtdis.util.ClientTypeMap;

@Scope("prototype")
@Component("decisionNeededApp")
public class DecisionNeededAppImpl //
		implements DecisionNeededApp {

	private static final String BASE = "-fx-text-base-color; ";

	private static final String BRIGHT_RED = "#ff0000; ";

	private static final String BRIGHT_GREEN = "#00ff00; ";

	@Autowired
	private AppButtonImpl decisionButton;

	@Autowired
	private AuditDialogImpl decisionDialog;

	@Autowired
	private AppFieldImpl<String> decidedByDisplay;

	@Autowired
	private AppFieldImpl<ZonedDateTime> decidedOnDisplay;

	@Autowired
	private LabelFactory label;

	@Autowired
	private MessageDialog dialog;

	@Autowired
	private ClientTypeMap map;

	private BooleanProperty canBeApproved = new SimpleBooleanProperty(false);

	private BooleanProperty canBeRejected = new SimpleBooleanProperty(false);

	private EventHandler<ActionEvent> event;

	private List<Node> decisionDisplays;

	@Override
	public List<Node> addApprovalNodes() {
		return decisionNodes("Validated");
	}

	private List<Node> decisionNodes(String s) {
		return decisionDisplays = asList(label.name(s + " by"), //
				decidedByDisplay.readOnly().width(120).build(TEXT), label.name("on"), //
				decidedOnDisplay.readOnly().build(TIMESTAMP));
	}

	@Override
	public List<Node> addAuditNodes() {
		return decisionNodes("Audited");
	}

	@Override
	public AppButtonImpl addDecisionButton() {
		return decisionButton.icon("decision").tooltip("Decide...").build();
	}

	@Override
	public void showDecisionNodesIf(ObservableBooleanValue b) {
		if (decisionDisplays != null) {
			decisionDisplays.forEach(n -> n.managedProperty().bind(b));
			decisionButton.managedProperty().bind(b);
		}
	}

	@Override
	public BooleanBinding isAudited() {
		return decisionDisplays == null ? null : decidedOnDisplay.isNotEmpty();
	}

	@Override
	public void refresh(DecisionNeededService service) {
		canBeApproved.set(service.canApprove());
		canBeRejected.set(service.canReject());
		refreshDisplays(service);
		refreshButton(service.getIsValid());
	}

	private void refreshDisplays(DecisionNeededService service) {
		if (decidedByDisplay == null)
			return;
		decidedByDisplay.setValue(service.getDecidedBy());
		decidedOnDisplay.setValue(service.getDecidedOn());
	}

	private void refreshButton(Boolean isValid) {
		if (isValid == null)
			updateButton("decision", BASE, "Decide...");
		else if (isValid)
			updateButton("accept", BRIGHT_GREEN, "Valid");
		else
			updateButton("reject", BRIGHT_RED, "Invalid");
	}

	private void updateButton(String icon, String color, String tooltip) {
		decisionButton.setText(map.icon(icon));
		decisionButton.getTooltip().setText(tooltip);
		decisionButton.onAction(color.equals(BASE) ? event : null);
		setButtonStyle(color);
	}

	private void setButtonStyle(String color) {
		String style = decisionButton.getStyle();
		decisionButton.setStyle(style + " -fx-text-fill: " + color);
	}

	@Override
	public void setDecisionButtonOnAction(EventHandler<ActionEvent> e) {
		if (decisionButton != null)
			decisionButton.onAction(event = e);
	}

	@Override
	public void showDecisionDialogForValidation(Stage stage, DecisionNeededService service) {
		if (decisionDialog == null)
			return;
		canBeApproved.set(service.canApprove());
		canBeRejected.set(service.canReject());
		decisionDialog(stage, service);
	}

	private void decisionDialog(Stage stage, DecisionNeededService service) {
		decisionDialog //
				.disableApprovalButtonIf(canBeApproved.not()) //
				.disableRejectionButtonIf(canBeRejected.not()) //
				.addParent(stage).start();
		if (isValid() != null)
			saveDecision(stage, service);
	}

	private void saveDecision(Stage stage, DecisionNeededService service) {
		try {
			saveDecision(service);
		} catch (Information i) {
			dialog.show(i).addParent(stage).start();
		} catch (Exception e) {
			e.printStackTrace();
			dialog.show(e).addParent(stage).start();
		}
	}

	private void saveDecision(DecisionNeededService service) throws Information, Exception {
		service.updatePerValidity(isValid(), remarks());
		((SavedService<?>) service).save();
	}

	private Boolean isValid() {
		return decisionDialog.isValid();
	}

	private String remarks() {
		return decisionDialog.getFindings();
	}
}
