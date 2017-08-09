package ph.txtdis.mgdc.gsm.app;

import javafx.beans.binding.BooleanBinding;
import javafx.scene.Node;
import javafx.scene.layout.HBox;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import ph.txtdis.app.DeliveryReportApp;
import ph.txtdis.mgdc.gsm.fx.table.PurchaseReceiptTable;
import ph.txtdis.mgdc.gsm.service.NoPurchaseOrderReceiptService;

import java.util.ArrayList;
import java.util.List;

@Scope("prototype")
@Component("purchaseReceiptApp")
public class PurchaseReceiptAppImpl //
	extends AbstractBillableApp<NoPurchaseOrderReceiptService, PurchaseReceiptTable, Long> //
	implements DeliveryReportApp {

	@Override
	protected List<Node> mainVerticalPaneNodes() {
		List<Node> l = new ArrayList<>(super.mainVerticalPaneNodes());
		l.add(trackedPane());
		return l;
	}

	@Override
	protected HBox trackedPane() {
		List<Node> l = new ArrayList<>(receivingNodes());
		l.addAll(decisionNodes());
		return pane.centeredHorizontal(l);
	}

	@Override
	protected void secondGridLine() {
	}

	@Override
	protected void addressAndOrRemarksGridLine() {
		remarksGridLineAtRowSpanning(1, 6);
	}

	@Override
	protected Node orderDateNode() {
		return orderDateStackPane();
	}

	@Override
	protected Node referenceNode() {
		referenceIdInput.setFieldWidth(180);
		return referenceIdInput;
	}

	@Override
	protected void renew() {
		super.renew();
		orderDatePicker.requestFocus();
	}

	@Override
	protected void setInputFieldBindings() {
		super.setInputFieldBindings();
		referenceIdInput.disableIf(isPosted() //
			.or(orderDateDisplay.isEmpty()));
		remarksDisplay.editableIf(isNew() //
			.and(referenceIdInput.isNotEmpty()));
	}

	@Override
	protected BooleanBinding isNew() {
		return receivedOnDisplay.isEmpty();
	}

	@Override
	protected void setListeners() {
		super.setListeners();
		referenceIdInput.onAction(e -> updateUponReferenceIdValidation());
		orderDatePicker.onAction(e -> service.setOrderDate(orderDatePicker.getValue()));
	}

	private void updateUponReferenceIdValidation() {
		Long id = referenceIdInput.getValue();
		if (service.isNew() && id != 0)
			try {
				service.updateUponReferenceIdValidation(id);
			} catch (Exception e) {
				handleError(e, referenceIdInput);
			}
	}

	@Override
	protected void setTableBindings() {
		table.disableIf(referenceIdInput.isEmpty());
	}

	@Override
	protected void topGridLine() {
		dateGridNodes();
		referenceGridNodes(service.getReferencePrompt(), 3);
		receivingGridNodes(service.getReceivingPrompt(), 5);
	}
}
