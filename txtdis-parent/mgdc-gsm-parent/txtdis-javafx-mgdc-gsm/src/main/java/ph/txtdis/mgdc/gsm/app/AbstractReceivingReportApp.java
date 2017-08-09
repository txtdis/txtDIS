package ph.txtdis.mgdc.gsm.app;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import ph.txtdis.mgdc.app.ReceivingReportApp;
import ph.txtdis.mgdc.fx.table.BillableTable;
import ph.txtdis.mgdc.gsm.service.ReceivingReportService;

import java.util.ArrayList;
import java.util.List;

public abstract class AbstractReceivingReportApp<AT extends BillableTable> //
	extends AbstractBillableApp<ReceivingReportService, AT, Long> //
	implements ReceivingReportApp {

	private BooleanProperty receivingReportCanBeCreated, receivingReportCanBeModified;

	@Override
	protected List<Node> mainVerticalPaneNodes() {
		List<Node> l = new ArrayList<>(super.mainVerticalPaneNodes());
		l.add(trackedPane());
		return l;
	}

	@Override
	protected HBox trackedPane() {
		List<Node> l = new ArrayList<>(receivingNodes());
		l.addAll(actionAfterReceivingNodes("Modified"));
		return pane.centeredHorizontal(l);
	}

	@Override
	protected Node referenceNode() {
		return referenceIdInput;
	}

	@Override
	protected void renew() {
		super.renew();
		referenceIdInput.requestFocus();
	}

	@Override
	protected void topGridLine() {
		dateGridNodes();
		referenceGridNodes(service.getReferencePrompt(), 3);
		receivingGridNodes(service.getReceivingPrompt(), 5);
	}

	@Override
	protected void secondGridLine() {
		customerWithoutDueDateGridLine(1, 6);
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

	private Label customerLabel() {
		return label.field("Customer");
	}

	@Override
	protected void addressAndOrRemarksGridLine() {
		remarksGridLineAtRowSpanning(2, 6);
	}

	@Override
	protected HBox tablePane() {
		return pane.centeredHorizontal(table.build());
	}

	@Override
	protected void setBooleanProperties() {
		receivingReportCanBeCreated = new SimpleBooleanProperty(service.isSalesOrderReturnable());
		receivingReportCanBeModified = new SimpleBooleanProperty(service.isReceivingReportModifiable());
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
		referenceIdInput.onAction(e -> updateUponReferenceIdValidation());
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

	@Override
	public void refresh() {
		super.refresh();
		afterReceivingActionByDisplay.setValue(service.getReceivingModifiedBy());
		afterReceivingActionOnDisplay.setValue(service.getReceivingModifiedOn());
		receivingReportCanBeCreated.set(service.isSalesOrderReturnable());
		receivingReportCanBeModified.set(service.isReceivingReportModifiable());
	}

	private void setFocusAfterReferenceIdValidation() {
		if (customerBox.isEmpty().not().get())
			table.requestFocus();
		else
			referenceIdInput.requestFocus();
	}
}
