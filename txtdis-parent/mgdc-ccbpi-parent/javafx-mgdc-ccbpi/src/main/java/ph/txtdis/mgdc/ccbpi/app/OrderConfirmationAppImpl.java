package ph.txtdis.mgdc.ccbpi.app;

import static org.apache.commons.lang3.StringUtils.substringBefore;
import static ph.txtdis.type.OrderConfirmationType.REGULAR;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javafx.scene.Node;
import ph.txtdis.app.StartableApp;
import ph.txtdis.app.TotaledTableApp;
import ph.txtdis.dto.BillableDetail;
import ph.txtdis.exception.NotFoundException;
import ph.txtdis.fx.control.AppCombo;
import ph.txtdis.fx.control.AppFieldImpl;
import ph.txtdis.fx.control.LocalDatePicker;
import ph.txtdis.mgdc.ccbpi.exception.NoDeliveryRouteException;
import ph.txtdis.mgdc.ccbpi.fx.dialog.CustomerDialogImpl;
import ph.txtdis.mgdc.ccbpi.fx.table.OrderConfirmationTable;
import ph.txtdis.mgdc.ccbpi.service.OrderConfirmationService;
import ph.txtdis.type.ModuleType;
import ph.txtdis.type.OrderConfirmationType;
import ph.txtdis.type.Type;

@Scope("prototype")
@Component("orderConfirmationApp")
public class OrderConfirmationAppImpl //
		extends AbstractBillableApp<OrderConfirmationService, OrderConfirmationTable, Long> //
		implements OrderConfirmationApp {

	@Autowired
	private AppCombo<OrderConfirmationType> typeCombo;

	@Autowired
	private AppFieldImpl<Long> sequenceIdDisplay;

	@Autowired
	private AppFieldImpl<String> deliveryDisplay, routeDisplay;

	@Autowired
	private LocalDatePicker dueDatePicker;

	@Autowired
	private CustomerDialogImpl outletDialog;

	@Autowired
	private TotaledTableApp<BillableDetail> totaledTableApp;

	@Override
	protected void addressAndOrRemarksGridLine() {
		remarksGridLineAtRowSpanning(2, 10);
		remarksDisplay.makeEditable();
	}

	@Override
	protected void buildFields() {
		super.buildFields();
		sequenceIdDisplay.readOnly().width(40).build(Type.ID);
		routeDisplay.readOnly().width(60).build(Type.TEXT);
		deliveryDisplay.readOnly().width(80).build(Type.TEXT);
	}

	@Override
	protected Node dueDateNode() {
		return stackPane(super.dueDateNode(), dueDatePicker);
	}

	@Override
	protected String getDialogInput() {
		openByIdDialog //
				.idPrompt(service.getOpenDialogKeyPrompt()) //
				.header(service.getOpenDialogHeader()) //
				.prompt(service.getOpenDialogPrompt()) //
				.addParent(this).start();
		return openByIdDialog.getKey();
	}

	@Override
	public void goToDefaultFocus() {
		newButton.requestFocus();
	}

	@Override
	protected List<Node> mainVerticalPaneNodes() {
		buildFields();
		return Arrays.asList( //
				gridPane(), //
				totaledTableApp.addNoSubHeadTablePane(table), //
				trackedPane());
	}

	@Override
	protected Node orderDateNode() {
		return orderDateStackPane();
	}

	@Override
	public void refresh() {
		super.refresh();
		refreshTitleAndHeader();
		refreshLogNodes();
		refreshOrderDate();
		customerBox.refresh();
		customerAddressDisplay.setValue(service.getCustomerAddress());
		refreshDeliveryDateAndTypeAndRouteAndDeliveryAndSequenceId();
		refreshTable();
	}

	private void refreshDeliveryDateAndTypeAndRouteAndDeliveryAndSequenceId() {
		refreshDeliveryDate();
		refreshType();
		refreshRoute();
		refreshDelivery();
		refreshSequenceId();
	}

	private void refreshDeliveryDate() {
		refreshDueDate();
		dueDatePicker.setValue(service.getDeliveryDate());
	}

	private void refreshSequenceId() {
		sequenceIdDisplay.setValue(service.getSequenceId());
	}

	private void refreshType() {
		typeCombo.items(service.listTypes());
		if (typeCombo.getItems().contains(REGULAR))
			typeCombo.select(REGULAR);
	}

	private void refreshRoute() {
		routeDisplay.setValue(service.getRoute());
	}

	private void refreshDelivery() {
		deliveryDisplay.setValue(service.getDelivery());
	}

	@Override
	protected void renew() {
		super.renew();
		orderDatePicker.requestFocus();
	}

	@Override
	protected void setBindings() {
		super.setBindings();
		orderDateBinding();
		setCustomerBoxBindings();
		setDueDateBindings();
	}

	private void setCustomerBoxBindings() {
		customerBox.disableIdInputIf(isPosted(). //
				or(orderDatePicker.isEmpty()));
		customerBox.setSearchButtonVisibleIfNot(isPosted());
	}

	private void setDueDateBindings() {
		dueDatePicker.disableIf(orderDatePicker.isEmpty());
		dueDatePicker.showIf(isPosted().not());
		dueDateDisplay.showIf(dueDatePicker.visibleProperty().not());
	}

	@Override
	protected void setInputFieldBindings() {
		super.setInputFieldBindings();
		remarksDisplay.editableIf(createdOnDisplay.isEmpty());
	}

	@Override
	protected void setListeners() {
		super.setListeners();
		orderDatePicker.onAction(e -> setOrderAndDeliveryDates());
		customerBox.onAction(e -> updateUponCustomerValidation(customerBox.getId()));
		dueDatePicker.onAction(e -> updateUponDeliveryDateValidation(dueDatePicker.getValue()));
		typeCombo.onAction(e -> updateUponTypeValidation());
	}

	private void setOrderAndDeliveryDates() {
		service.resetAndSetOrderDate(orderDatePicker.getValue());
		refresh();
		customerBox.requestFocus();
	}

	private void updateUponCustomerValidation(Long id) {
		if (service.isNew() && id != null && id != 0)
			try {
				service.updateUponCustomerVendorIdValidation(id);
			} catch (NoDeliveryRouteException e) {
				showUpdateOutletDialogThenResetData(id, e);
			} catch (NotFoundException e) {
				showCreateNewOutletOrExitDialog(e, id);
			} catch (Exception e) {
				customerBox.handleError(e);
			} finally {
				customerBox.refresh();
				refreshDeliveryDateAndTypeAndRouteAndDeliveryAndSequenceId();
				refreshTable();
				customerBox.setFocusAfterCustomerValidation(dueDatePicker);
			}
	}

	private void showUpdateOutletDialogThenResetData(Long id, NoDeliveryRouteException e) {
		showErrorDialog(e);
		openOutletDialog(outletName(e), id);
		service.reset();
		service.setOrderDate(orderDatePicker.getValue());
		customerBox.setId(id);
	}

	private String outletName(NoDeliveryRouteException e) {
		return substringBefore(e.getMessage(), "\n");
	}

	private void updateUponDeliveryDateValidation(LocalDate due) {
		if (service.isNew())
			try {
				service.setDeliveryDateUponValidation(due);
			} catch (Exception e) {
				due = null;
				showErrorDialog(e);
			} finally {
				refreshDeliveryDate();
				refreshType();
				refreshTable();
				setFocusAfterDaliveryDateValidation(due);
			}
	}

	private void setFocusAfterDaliveryDateValidation(LocalDate due) {
		if (due == null)
			dueDatePicker.requestFocus();
	}

	private void updateUponTypeValidation() {
		try {
			service.updateUponTypeValidation(typeCombo.getValue());
			refreshTable();
		} catch (Exception e) {
			handleError(e, typeCombo);
		}
	}

	@Override
	public void start() {
		totaledTableApp.addTotalDisplays(2);
		super.start();
	}

	@Override
	protected void updateSummaries() {
		super.updateSummaries();
		totaledTableApp.refresh(service);
	}

	@Override
	protected void setTableBindings() {
		table.disableIf(typeCombo.disableProperty());
	}

	private void showCreateNewOutletOrExitDialog(Exception x, Long id) {
		dialog.showOption(x.getMessage(), "Create", "Exit");
		dialog.setOnOptionSelection(e -> openOutletDialog(null, id));
		dialog.setOnDefaultSelection(e -> exitDialog());
		dialog.addParent(this).start();
	}

	private void exitDialog() {
		dialog.close();
		customerBox.refresh();
	}

	private void openOutletDialog(String name, Long id) {
		outletDialog.outletName(name).vendorId(id).addParent(this).start();
		exitDialog();
		setCustomer(name);
	}

	private void setCustomer(String name) {
		if (name == null)
			try {
				service.setCustomer(outletDialog.getCustomer());
			} catch (Exception e) {
				e.printStackTrace();
			}
	}

	@Override
	protected void secondGridLine() {
		dueDateGridNodes(1);
		gridPane.add(label.field("Type"), 3, 1);
		gridPane.add(typeCombo, 4, 1);
		gridPane.add(label.field("Route"), 5, 1);
		gridPane.add(routeDisplay, 6, 1);
		gridPane.add(label.field("Delivery"), 7, 1);
		gridPane.add(deliveryDisplay, 8, 1);
		gridPane.add(label.field("Sequence"), 9, 1);
		gridPane.add(sequenceIdDisplay, 10, 1);
	}

	@Override
	protected void topGridLine() {
		dateGridNodes();
		customerGridNodes(0);
	}

	@Override
	public StartableApp type(ModuleType type) {
		return this;
	}

	@Override
	public ModuleType type() {
		return ModuleType.ORDER_CONFIRMATION;
	}
}
