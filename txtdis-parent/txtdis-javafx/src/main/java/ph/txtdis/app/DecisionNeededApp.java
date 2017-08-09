package ph.txtdis.app;

import javafx.beans.binding.BooleanBinding;
import javafx.beans.value.ObservableBooleanValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.stage.Stage;
import ph.txtdis.fx.control.AppButton;
import ph.txtdis.service.DecisionNeededService;

import java.util.List;

public interface DecisionNeededApp {

	List<Node> addApprovalNodes();

	List<Node> addAuditNodes();

	AppButton addDecisionButton();

	void showDecisionNodesIf(ObservableBooleanValue b);

	BooleanBinding isAudited();

	void refresh(DecisionNeededService service);

	void setDecisionButtonOnAction(EventHandler<ActionEvent> e);

	void showDecisionDialogForValidation(Stage stage, DecisionNeededService service);
}