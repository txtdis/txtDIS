package ph.txtdis.app;

import static java.util.Arrays.asList;
import static ph.txtdis.type.Type.CURRENCY;
import static ph.txtdis.type.Type.DATE;
import static ph.txtdis.type.Type.ID;
import static ph.txtdis.type.Type.TEXT;
import static ph.txtdis.type.Type.TIMESTAMP;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.scene.Node;
import javafx.scene.control.Control;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import ph.txtdis.dto.Billable;
import ph.txtdis.fx.control.AppButton;
import ph.txtdis.fx.control.AppCombo;
import ph.txtdis.fx.control.AppField;
import ph.txtdis.fx.control.InputControl;
import ph.txtdis.fx.control.LocalDatePicker;
import ph.txtdis.fx.pane.AppGridPane;
import ph.txtdis.fx.pane.CustomerBox;
import ph.txtdis.fx.table.BillableTable;
import ph.txtdis.info.Information;
import ph.txtdis.service.BillableService;

public abstract class AbstractBillableApp<AS extends BillableService, AT extends BillableTable, PK> //
		extends AbstractIdApp<AS, Billable, Long, PK> //
		implements BillableApp {

	protected static final String PROMPT = "Select date whose first entry will opened";

	protected static final String ON = "on";

	@Autowired
	private AppButton openByDateButton;

	@Autowired
	private AppField<String> billedByDisplay, customerAddressDisplay, printedByDisplay;

	@Autowired
	protected CustomerBox customerBox;

	@Autowired
	protected AppButton invalidateButton;

	@Autowired
	protected AppCombo<String> paymentCombo;

	@Autowired
	protected AppField<BigDecimal> balanceDisplay, totalDisplay, vatableDisplay, vatDisplay;

	@Autowired
	protected AppField<LocalDate> dueDateDisplay, orderDateDisplay;

	@Autowired
	protected AppField<Long> receivingIdDisplay, referenceIdInput;

	@Autowired
	protected AppField<String> afterReceivingActionByDisplay, receivedByDisplay;

	@Autowired
	protected AppField<ZonedDateTime> afterReceivingActionOnDisplay, billedOnDisplay, printedOnDisplay,
			receivedOnDisplay;

	@Autowired
	protected AT table;

	@Autowired
	protected LocalDatePicker orderDatePicker;

	protected BooleanProperty isOffSite;

	@Override
	protected List<AppButton> addButtons() {
		buildButttons();
		List<AppButton> b = new ArrayList<>(super.addButtons());
		b.add(2, openByDateButton);
		return b;
	}

	protected void buildButttons() {
		decisionButton = decisionNeededApp.addDecisionButton();
		invalidateButton.icon("deactivate").tooltip("Invalidate...").build();
		openByDateButton.icon("openByDate").tooltip("Open a date's\nfirst entry").build();
	}

	@Override
	protected List<Node> mainVerticalPaneNodes() {
		buildDisplays();
		return asList(gridPane(), tablePane());
	}

	protected void buildDisplays() {
		afterReceivingActionByDisplay.readOnly().width(120).build(TEXT);
		afterReceivingActionOnDisplay.readOnly().build(TIMESTAMP);
		balanceDisplay.readOnly().build(CURRENCY);
		billedByDisplay.readOnly().width(120).build(TEXT);
		billedOnDisplay.readOnly().build(TIMESTAMP);
		customerAddressDisplay.readOnly().build(TEXT);
		customerBox.build(this, service);
		dueDateDisplay.readOnly().build(DATE);
		orderDateDisplay.readOnly().build(DATE);
		paymentCombo.readOnlyOfWidth(420);
		printedByDisplay.readOnly().width(120).build(TEXT);
		printedOnDisplay.readOnly().build(TIMESTAMP);
		receivedByDisplay.readOnly().width(120).build(TEXT);
		receivedOnDisplay.readOnly().build(TIMESTAMP);
		receivingIdDisplay.readOnly().build(ID);
		referenceIdInput.build(ID);
		remarksDisplay.build();
		totalDisplay.readOnly().build(CURRENCY);
		vatableDisplay.readOnly().build(CURRENCY);
		vatDisplay.readOnly().build(CURRENCY);
	}

	protected AppGridPane gridPane() {
		gridPane.getChildren().clear();
		topGridLine();
		customerGridLine();
		addressAndOrRemarksGridLines();
		return gridPane;
	}

	protected abstract void topGridLine();

	protected abstract void customerGridLine();

	protected abstract void addressAndOrRemarksGridLines();

	protected HBox tablePane() {
		return box.forHorizontalPane(table.build());
	}

	protected List<Node> addDecisionNodes() {
		return decisionNeededApp.addApprovalNodes();
	}

	protected void addRemarksAtLine(int lineId, int columnSpan) {
		gridPane.add(label.field("Remarks"), 0, lineId);
		gridPane.add(remarksDisplay.get(), 1, lineId, columnSpan, 2);
	}

	protected void addressGridNodes() {
		gridPane.add(label.field("Address"), 0, 2);
		gridPane.add(customerAddressDisplay, 1, 2, 8, 1);
	}

	protected List<Node> billingNodes(String prompt) {
		return asList( //
				label.name(prompt + " by"), billedByDisplay, //
				label.name(ON), billedOnDisplay);
	}

	protected String createdByLabelName() {
		return "Booked by";
	}

	protected HBox customerBox() {
		return customerBox.get();
	}

	private Label customerLabel() {
		return label.field("Customer");
	}

	protected void customerWithDueDateGridLine() {
		gridPane.add(label.field("Due"), 0, 1);
		gridPane.add(dueDateNode(), 1, 1);
		gridPane.add(customerLabel(), 2, 1, 2, 1);
		gridPane.add(customerBox(), 4, 1, 8, 1);
	}

	protected void customerWithoutDueDateGridLine() {
		gridPane.add(customerLabel(), 0, 1);
		gridPane.add(customerBox(), 1, 1, 6, 1);
	}

	protected void dateGridNodes() {
		gridPane.add(label.field("Date"), 0, 0);
		gridPane.add(orderDateNode(), 1, 0, 2, 1);
	}

	protected Node dueDateNode() {
		return dueDateDisplay;
	}

	protected void handleError(Exception e, InputControl<?> control) {
		showErrorDialog(e);
		control.setValue(null);
		((Node) control).requestFocus();
	}

	@Override
	protected void openSelected() {
		String id = getDialogInput();
		if (id != null && !id.isEmpty())
			actOn(id);
	}

	protected Node orderDateDisplayOnly() {
		orderDatePicker.setDisable(true);
		return orderDateDisplay;
	}

	protected Node orderDateNode() {
		return orderDateDisplayOnly();
	}

	protected Node orderDateStackPane() {
		return stackPane(orderDateDisplay, orderDatePicker);
	}

	protected List<Node> paymentNodes() {
		return asList(//
				label.name("Payment"), paymentCombo, //
				label.name("Balance"), balanceDisplay);
	}

	protected List<Node> printNodes() {
		return new ArrayList<>(asList(//
				label.name("Printed by"), printedByDisplay, //
				label.name(ON), printedOnDisplay));
	}

	protected void receivingGridNodes(String rrPrompt, int column) {
		gridPane.add(label.field(rrPrompt), column, 0);
		gridPane.add(receivingIdDisplay, ++column, 0);
	}

	protected List<Node> receivingNodes() {
		return asList(//
				label.name("Received by"), receivedByDisplay, //
				label.name(ON), receivedOnDisplay);
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
		customerBox.refresh();
		billedByDisplay.setValue(service.getBilledBy());
		billedOnDisplay.setValue(service.getBilledOn());
		customerAddressDisplay.setValue(service.getCustomerAddress());
		dueDateDisplay.setValue(service.getDueDate());
		orderDateDisplay.setValue(service.getOrderDate());
		orderDatePicker.setValue(service.getOrderDate());
		printedByDisplay.setValue(service.getPrintedBy());
		printedOnDisplay.setValue(service.getPrintedOn());
		receivedByDisplay.setValue(service.getReceivedBy());
		receivedOnDisplay.setValue(service.getReceivedOn());
		receivingIdDisplay.setValue(service.getReceivingId());
		referenceIdInput.setValue(service.getBookingId());
		remarksDisplay.setValue(service.getRemarks());
		table.items(service.getDetails());
	}

	@Override
	public void save() {
		try {
			service.setRemarks(remarksDisplay.getValue());
			saveBillable();
		} catch (Information i) {
			dialog.show(i).addParent(this).start();
		} catch (Exception e) {
			showErrorDialog(e);
		} finally {
			refresh();
		}
	}

	protected void saveBillable() throws Information, Exception {
		service.save();
	}

	@Override
	protected void setBindings() {
		setBooleanProperties();
		setButtonBindings();
		setInputFieldBindings();
		setTableBindings();
	}

	protected void setBooleanProperties() {
		isOffSite = new SimpleBooleanProperty(service.isOffSite());
	}

	protected abstract void setButtonBindings();

	protected void setInputFieldBindings() {
		referenceIdInput.disableIf(isPosted());
		remarksDisplay.editableIf(isPosted().not() //
				.or(customerBox.isNameDisplayEmpty().not()));
	}

	protected void setTableBindings() {
		table.disableIf(customerBox.isNameDisplayEmpty());
	}

	@Override
	public void setFocus() {
		newButton.requestFocus();
	}

	@Override
	protected void setListeners() {
		super.setListeners();
		openByDateButton.setOnAction(e -> showOpenByDateDialog());
	}

	protected void showOpenByDateDialog() {
		String h = service.getOpenDialogHeader();
		openByDateDialog.header(h).prompt(PROMPT).addParent(this).start();
		LocalDate d = openByDateDialog.getDate();
		if (d != null)
			open(d);
	}

	protected List<Node> totalNodes() {
		return asList(label.name("Total"), totalDisplay);
	}

	protected List<Node> vatNodes() {
		List<Node> l = new ArrayList<>(asList(//
				label.name("VATable"), vatableDisplay, //
				label.name("VAT"), vatDisplay));
		l.addAll(totalNodes());
		return l;
	}

	protected Node vatPane() {
		return box.forHorizontalPane(vatNodes());
	}

	protected HBox actionAfterReceivingPane(String prompt) {
		return box.forHorizontalPane(actionAfterReceivingNodes(prompt));
	}

	protected List<Control> actionAfterReceivingNodes(String prompt) {
		return asList(//
				label.name(prompt + " by"), afterReceivingActionByDisplay, //
				label.name(ON), afterReceivingActionOnDisplay);
	}
}
