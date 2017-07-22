package ph.txtdis.mgdc.app;

import static ph.txtdis.type.Type.DATE;

import java.util.ArrayList;
import java.util.List;

import javafx.scene.Node;
import ph.txtdis.app.AbstractRemarkedKeyedApp;
import ph.txtdis.app.BillableApp;
import ph.txtdis.dto.Keyed;
import ph.txtdis.fx.control.AppButtonImpl;
import ph.txtdis.service.DecisionNeededService;
import ph.txtdis.service.RemarkedAndSpunAndSavedAndOpenDialogAndTitleAndHeaderAndIconAndModuleNamedAndResettableAndTypeMappedService;

public abstract class AbstractDecisionNeededApp< //
		AS extends RemarkedAndSpunAndSavedAndOpenDialogAndTitleAndHeaderAndIconAndModuleNamedAndResettableAndTypeMappedService<PK>, //
		T extends Keyed<PK>, //
		PK, //
		ID> //
		extends AbstractRemarkedKeyedApp<AS, T, PK, ID> //
		implements BillableApp {

	@Override
	protected List<AppButtonImpl> addButtons() {
		buildButttons();
		List<AppButtonImpl> b = new ArrayList<>(super.addButtons());
		b.add(decisionButton);
		return b;
	}

	protected void buildButttons() {
		decisionButton = decisionNeededApp.addDecisionButton();
	}

	@Override
	public void refresh() {
		super.refresh();
		decisionNeededApp.refresh((DecisionNeededService) service);
		remarksDisplay.setValue(((DecisionNeededService) service).getRemarks());
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

	protected void buildFields() {
		orderDateDisplay.readOnly().build(DATE);
		remarksDisplay.build();
	}

	protected List<Node> decisionNodes() {
		return decisionNeededApp.addApprovalNodes();
	}

	protected Node decisionPane() {
		return box.forHorizontalPane(decisionNodes());
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
