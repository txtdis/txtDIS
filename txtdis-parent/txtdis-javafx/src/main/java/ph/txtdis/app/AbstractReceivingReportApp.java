package ph.txtdis.app;

import java.util.ArrayList;
import java.util.List;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import ph.txtdis.fx.table.BillableTable;
import ph.txtdis.service.ReceivingReportService;

public abstract class AbstractReceivingReportApp<AT extends BillableTable>
		extends AbstractBillableApp<ReceivingReportService, AT, Long> implements ReceivingReportApp {

	private BooleanProperty receivingReportCanBeCreated, receivingReportCanBeModified;

	@Override
	protected List<Node> mainVerticalPaneNodes() {
		List<Node> l = new ArrayList<>(super.mainVerticalPaneNodes());
		l.add(trackedPane());
		return l;
	}

	@Override
	protected Node referenceNode() {
		return referenceIdInput;
	}

	@Override
	protected void reset() {
		super.reset();
		referenceIdInput.requestFocus();
	}

	@Override
	protected void topGridLine() {
		dateGridNodes();
		referenceGridNodes(service.getReferencePrompt(), 3);
		receivingGridNodes(service.getReceivingPrompt(), 5);
	}

	@Override
	protected HBox trackedPane() {
		List<Node> l = new ArrayList<>(receivingNodes());
		l.addAll(actionAfterReceivingNodes("Modified"));
		return box.forHorizontalPane(l);
	}

	@Override
	protected void customerGridLine() {
		customerWithoutDueDateGridLine();
	}

	@Override
	protected void customerWithoutDueDateGridLine() {
		gridPane.add(customerLabel(), 0, 1);
		gridPane.add(customerBox(), 1, 1, 6, 1);
	}

	private Label customerLabel() {
		return label.field("Customer");
	}

	@Override
	protected void customerWithDueDateGridLine() {
		gridPane.add(label.field("Due"), 0, 1);
		gridPane.add(dueDateNode(), 1, 1);
		gridPane.add(customerLabel(), 2, 1, 2, 1);
		gridPane.add(customerBox(), 4, 1, 8, 1);
	}

	@Override
	protected Node dueDateNode() {
		return dueDateDisplay;
	}

	@Override
	protected void addressAndOrRemarksGridLines() {
		addRemarksAtLine(2, 6);
	}

	@Override
	protected HBox tablePane() {
		return box.forHorizontalPane(table.build());
	}

	@Override
	public void refresh() {
		super.refresh();
		afterReceivingActionByDisplay.setValue(service.getReceivingModifiedBy());
		afterReceivingActionOnDisplay.setValue(service.getReceivingModifiedOn());
		receivingReportCanBeCreated.set(service.isSalesOrderReturnable());
		receivingReportCanBeModified.set(service.isReceivingReportModifiable());
	}

	@Override
	protected void setBooleanProperties() {
		super.setBooleanProperties();
		receivingReportCanBeCreated = new SimpleBooleanProperty(service.isSalesOrderReturnable());
		receivingReportCanBeModified = new SimpleBooleanProperty(service.isReceivingReportModifiable());
	}

	@Override
	protected void setButtonBindings() {
		decisionButton.disableIf(notPosted());
		saveButton.disableIf(isPosted() //
				.or(table.isEmpty()));
	}

	@Override
	protected void setInputFieldBindings() {
		super.setInputFieldBindings();
		customerBox.disableIdInput();
		customerBox.hideSearchButton();
	}

	@Override
	protected void setListeners() {
		super.setListeners();
		referenceIdInput.setOnAction(e -> updateUponReferenceIdValidation());
		table.setOnItemChange(e -> service.setDetails(table.getItems()));
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
		if (customerBox.isNameDisplayEmpty().not().get())
			table.requestFocus();
		else
			referenceIdInput.requestFocus();
	}
}
