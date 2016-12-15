package ph.txtdis.app;

import java.util.ArrayList;
import java.util.List;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javafx.beans.binding.BooleanBinding;
import javafx.scene.Node;
import javafx.scene.layout.HBox;
import ph.txtdis.fx.table.PurchaseReceiptTable;
import ph.txtdis.service.NoPurchaseOrderReceiptService;

@Scope("prototype")
@Component("purchaseReceiptApp")
public class NoPurchaseOrderReceiptAppImpl extends
		AbstractBillableApp<NoPurchaseOrderReceiptService, PurchaseReceiptTable, Long> implements PurchaseReceiptApp {

	@Override
	protected void addressAndOrRemarksGridLines() {
		addRemarksAtLine(1, 6);
	}

	@Override
	protected void customerGridLine() {
	}

	@Override
	protected BooleanBinding isPosted() {
		return receivedOnDisplay.isNotEmpty();
	}

	@Override
	protected List<Node> mainVerticalPaneNodes() {
		List<Node> l = new ArrayList<>(super.mainVerticalPaneNodes());
		l.add(trackedPane());
		return l;
	}

	// TODO to remove later
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
	protected void reset() {
		super.reset();
		referenceIdInput.requestFocus();
	}

	@Override
	protected void setButtonBindings() {
		saveButton.disableIf(isPosted() //
				.or(table.isEmpty()));
	}

	@Override
	protected void setListeners() {
		super.setListeners();
		referenceIdInput.setOnAction(e -> updateUponReferenceIdValidation());
		table.setOnItemChange(e -> service.setDetails(table.getItems()));
		// TODO -- to remove later
		orderDatePicker.setOnAction(e -> service.setOrderDate(orderDatePicker.getValue()));
	}

	@Override
	protected void setTableBindings() {
		table.disableIf(referenceIdInput.isEmpty());
	}

	private void updateUponReferenceIdValidation() {
		Long id = referenceIdInput.getValue();
		if (service.isNew() && id != 0)
			try {
				service.updateUponReferenceIdValidation(id);
			} catch (Exception e) {
				handleError(e, referenceIdInput);
			} finally {
				refresh();
				setFocusAfterReferenceIdValidation();
			}
	}

	private void setFocusAfterReferenceIdValidation() {
		if (referenceIdInput.isEmpty().get())
			referenceIdInput.requestFocus();
		else
			table.requestFocus();
	}

	@Override
	protected void topGridLine() {
		dateGridNodes();
		referenceGridNodes(service.getReferencePrompt(), 3);
		receivingGridNodes(service.getReceivingPrompt(), 5);
	}

	@Override
	protected HBox trackedPane() {
		return box.forHorizontalPane(receivingNodes());
	}
}
