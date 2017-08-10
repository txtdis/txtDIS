package ph.txtdis.mgdc.gsm.app;

import javafx.beans.binding.BooleanBinding;
import javafx.scene.Node;
import javafx.scene.control.Control;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import org.springframework.beans.factory.annotation.Autowired;
import ph.txtdis.app.BillableApp;
import ph.txtdis.dto.Billable;
import ph.txtdis.fx.control.AppButton;
import ph.txtdis.fx.control.AppCombo;
import ph.txtdis.fx.control.AppFieldImpl;
import ph.txtdis.fx.pane.AppGridPane;
import ph.txtdis.mgdc.app.AbstractDecisionNeededApp;
import ph.txtdis.mgdc.fx.table.BillableTable;
import ph.txtdis.mgdc.gsm.fx.pane.CustomerBox;
import ph.txtdis.mgdc.gsm.service.BillableService;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

import static java.util.Arrays.asList;
import static ph.txtdis.type.Type.*;

public abstract class AbstractBillableApp<
	BS extends BillableService,
	BT extends BillableTable,
	PK>
	extends AbstractDecisionNeededApp<BS, Billable, Long, PK> 
	implements BillableApp {

	protected static final String PROMPT = "Select date whose first entry will opened";

	protected static final String ON = "on";

	@Autowired
	protected AppButton invalidateButton;

	@Autowired
	protected AppCombo<String> paymentCombo;

	@Autowired
	protected AppFieldImpl<BigDecimal> balanceDisplay, totalDisplay, vatableDisplay, vatDisplay;

	@Autowired
	protected AppFieldImpl<LocalDate> dueDateDisplay;

	@Autowired
	protected AppFieldImpl<Long> receivingIdDisplay, referenceIdInput;

	@Autowired
	protected AppFieldImpl<String> customerAddressDisplay, afterReceivingActionByDisplay, billedByDisplay,
		orderNoDisplay, printedByDisplay,
		receivedByDisplay;

	@Autowired
	protected AppFieldImpl<ZonedDateTime> afterReceivingActionOnDisplay, billedOnDisplay, printedOnDisplay,
		receivedOnDisplay;

	@Autowired
	protected CustomerBox customerBox;

	@Autowired
	protected BT table;

	@Override
	protected void buildButttons() {
		super.buildButttons();
		invalidateButton.icon("deactivate").tooltip("Invalidate...").build();
	}

	@Override
	protected void clear() {
		super.clear();
		table.removeListener();
	}

	@Override
	protected String createdByLabelName() {
		return "Booked by";
	}

	@Override
	protected List<Node> mainVerticalPaneNodes() {
		buildFields();
		return asList(gridPane(), tablePane());
	}

	@Override
	protected void buildFields() {
		super.buildFields();
		afterReceivingActionByDisplay.readOnly().width(120).build(TEXT);
		afterReceivingActionOnDisplay.readOnly().build(TIMESTAMP);
		balanceDisplay.readOnly().build(CURRENCY);
		billedByDisplay.readOnly().width(120).build(TEXT);
		billedOnDisplay.readOnly().build(TIMESTAMP);
		customerAddressDisplay.readOnly().build(TEXT);
		customerBox.build(this, service);
		dueDateDisplay.readOnly().build(DATE);
		paymentCombo.readOnlyOfWidth(480);
		printedByDisplay.readOnly().width(120).build(TEXT);
		printedOnDisplay.readOnly().build(TIMESTAMP);
		receivedByDisplay.readOnly().width(120).build(TEXT);
		receivedOnDisplay.readOnly().build(TIMESTAMP);
		receivingIdDisplay.readOnly().build(ID);
		referenceIdInput.build(ID);
		totalDisplay.readOnly().build(CURRENCY);
		vatableDisplay.readOnly().build(CURRENCY);
		vatDisplay.readOnly().build(CURRENCY);
	}

	protected AppGridPane gridPane() {
		gridPane.getChildren().clear();
		topGridLine();
		secondGridLine();
		addressAndOrRemarksGridLine();
		return gridPane;
	}

	protected HBox tablePane() {
		return pane.centeredHorizontal(table.build());
	}

	protected abstract void topGridLine();

	protected abstract void secondGridLine();

	protected void addressAndOrRemarksGridLine() {
		addressGridNodes(2, 8);
		remarksGridLineAtRowSpanning(3, 8);
	}

	protected void addressGridNodes(int lineId, int columnSpan) {
		gridPane.add(label.field("Address"), 0, lineId);
		gridPane.add(customerAddressDisplay, 1, lineId, columnSpan, 1);
	}

	protected void customerWithDueDateGridLine() {
		dueDateGridNodes(1);
		customerGridNodes(1);
	}

	protected void dueDateGridNodes(int row) {
		gridPane.add(label.field("Due"), 0, row);
		gridPane.add(dueDateNode(), 1, row);
	}

	protected void customerGridNodes(int row) {
		gridPane.add(customerLabel(), 2, row, 2, 1);
		gridPane.add(customerBox(), 4, row, 8, 1);
	}

	protected Node dueDateNode() {
		return dueDateDisplay;
	}

	private Label customerLabel() {
		return label.field("Customer");
	}

	protected HBox customerBox() {
		return customerBox.get();
	}

	protected void dateGridNodes() {
		gridPane.add(label.field("Date"), 0, 0);
		gridPane.add(orderDateNode(), 1, 0, 2, 1);
	}

	protected Node orderDateNode() {
		return orderDateDisplayOnly();
	}

	protected Node orderDateDisplayOnly() {
		orderDatePicker.setDisable(true);
		return orderDateDisplay;
	}

	@Override
	protected void openSelected() {
		String id = getDialogInput();
		if (id != null && !id.isEmpty())
			actOn(id);
	}

	protected void referenceGridNodes(String prompt, int column) {
		gridPane.add(label.field(prompt), column, 0);
		gridPane.add(referenceNode(), ++column, 0);
	}

	protected Node referenceNode() {
		return referenceIdInput;
	}

	@Override
	public void refresh() {
		super.refresh();
		refreshOrderDate();
		refreshDueDate();
		customerBox.refresh();
		customerAddressDisplay.setValue(service.getCustomerAddress());
		billedByDisplay.setValue(service.getBilledBy());
		billedOnDisplay.setValue(service.getBilledOn());
		printedByDisplay.setValue(service.getPrintedBy());
		printedOnDisplay.setValue(service.getPrintedOn());
		receivedByDisplay.setValue(service.getReceivedBy());
		receivedOnDisplay.setValue(service.getReceivedOn());
		receivingIdDisplay.setValue(service.getReceivingId());
		referenceIdInput.setValue(service.getBookingId());
		refreshTable();
		refreshSummaryPane();
	}

	protected void refreshOrderDate() {
		orderDateDisplay.setValue(service.getOrderDate());
		orderDatePicker.setValue(service.getOrderDate());
	}

	protected void refreshDueDate() {
		dueDateDisplay.setValue(service.getDueDate());
	}

	protected void refreshTable() {
		table.items(service.getDetails());
	}

	protected void refreshSummaryPane() {
		totalDisplay.setValue(service.getTotalValue());
	}

	@Override
	protected void setBindings() {
		setBooleanProperties();
		setButtonBindings();
		setInputFieldBindings();
		setTableBindings();
	}

	protected void setBooleanProperties() {
	}

	@Override
	protected void setButtonBindings() {
		super.setButtonBindings();
		setSaveButtonBinding();
	}

	@Override
	protected void setInputFieldBindings() {
		orderDateBinding();
		setReferenceIdBindings();
		remarksDisplay.editableIf(isNew() 
			.and(customerBox.isEmpty().not()));
	}

	protected void setTableBindings() {
		table.disableIf(customerBox.isEmpty());
	}

	protected void setSaveButtonBinding() {
		saveButton.disableIf(saveButtonDisableBinding());
	}

	protected void setReferenceIdBindings() {
		referenceIdInput.disableIf(isPosted());
	}

	protected BooleanBinding saveButtonDisableBinding() {
		return isPosted() 
			.or(table.isEmpty());
	}

	@Override
	public void goToDefaultFocus() {
		newButton.requestFocus();
	}

	@Override
	protected void setListeners() {
		super.setListeners();
		table.setOnItemChange(e -> updateSummaries());
	}

	protected void updateSummaries() {
		service.updateSummaries(table.getItems());
		refreshSummaryPane();
	}

	protected HBox OnionAfterReceivingPane(String prompt) {
		return pane.centeredHorizontal(actionAfterReceivingNodes(prompt));
	}

	protected List<Control> actionAfterReceivingNodes(String prompt) {
		return asList(
			label.name(prompt + " by"), afterReceivingActionByDisplay, 
			label.name(ON), afterReceivingActionOnDisplay);
	}

	protected List<Node> billingNodes(String prompt) {
		return asList(
			label.name(prompt + " by"), billedByDisplay, 
			label.name(ON), billedOnDisplay);
	}

	protected void customerWithoutDueDateGridLine(int row, int columnSpan) {
		gridPane.add(customerLabel(), 0, row);
		gridPane.add(customerBox(), 1, row, columnSpan, 1);
	}

	protected List<Node> printNodes() {
		return new ArrayList<>(asList(
			label.name("Printed by"), printedByDisplay, 
			label.name(ON), printedOnDisplay));
	}

	protected void receivingGridNodes(String rrPrompt, int column) {
		gridPane.add(label.field(rrPrompt), column, 0);
		gridPane.add(receivingIdDisplay, ++column, 0);
	}

	protected List<Node> receivingNodes() {
		return asList(
			label.name("Received by"), receivedByDisplay, 
			label.name(ON), receivedOnDisplay);
	}

	protected Node vatAndTotalPane() {
		List<Node> l = vatAndTotalNodes();
		return pane.centeredHorizontal(l);
	}

	protected List<Node> vatAndTotalNodes() {
		List<Node> l = new ArrayList<>(vatNodes());
		l.addAll(totalNodes());
		return l;
	}

	protected List<Node> vatNodes() {
		return asList(
			label.name("VATable"), vatableDisplay, 
			label.name("VAT"), vatDisplay);
	}

	protected List<Node> totalNodes() {
		return asList(label.name("Total"), totalDisplay);
	}
}
