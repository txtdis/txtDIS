package ph.txtdis.mgdc.app;

import javafx.scene.Node;
import ph.txtdis.app.AbstractRemarkedKeyedApp;
import ph.txtdis.app.BillableApp;
import ph.txtdis.dto.Keyed;
import ph.txtdis.fx.control.AppButton;
import ph.txtdis.service.DecisionNeededService;
import ph.txtdis.service
	.RemarkedAndSpunAndSavedAndOpenDialogAndTitleAndHeaderAndIconAndModuleNamedAndResettableAndTypeMappedService;

import java.util.ArrayList;
import java.util.List;

import static ph.txtdis.type.Type.DATE;

public abstract class AbstractDecisionNeededApp< 
	AS extends
		RemarkedAndSpunAndSavedAndOpenDialogAndTitleAndHeaderAndIconAndModuleNamedAndResettableAndTypeMappedService<PK>,
	T extends Keyed<PK>, 
	PK, 
	ID> 
	extends AbstractRemarkedKeyedApp<AS, T, PK, ID> 
	implements BillableApp {

	@Override
	protected List<AppButton> addButtons() {
		buildButttons();
		List<AppButton> b = new ArrayList<>(super.addButtons());
		b.add(decisionButton);
		return b;
	}

	protected void buildButttons() {
		decisionButton = decisionNeededApp.addDecisionButton();
	}

	@Override
	protected void setListeners() {
		super.setListeners();
		decisionNeededApp.setDecisionButtonOnAction(e -> showAuditDialogToValidateOrder());
	}

	protected void showAuditDialogToValidateOrder() {
		decisionNeededApp.showDecisionDialogForValidation(this, (DecisionNeededService) service);
		refresh();
	}

	@Override
	public void refresh() {
		super.refresh();
		decisionNeededApp.refresh((DecisionNeededService) service);
		remarksDisplay.setValue(((DecisionNeededService) service).getRemarks());
	}

	protected void buildFields() {
		orderDateDisplay.readOnly().build(DATE);
		remarksDisplay.build();
	}

	protected Node decisionPane() {
		return pane.centeredHorizontal(decisionNodes());
	}

	protected List<Node> decisionNodes() {
		return decisionNeededApp.addApprovalNodes();
	}

	protected void remarksGridLineAtRowSpanning(int lineId, int columnSpan) {
		gridPane.add(label.field("Remarks"), 0, lineId);
		gridPane.add(remarksDisplay.get(), 1, lineId, columnSpan, 2);
	}

	protected void setButtonBindings() {
		decisionButton.disableIf(isNew());
	}

	protected void setInputFieldBindings() {
		remarksDisplay.editableIf(isNew());
	}
}
