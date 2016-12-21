package ph.txtdis.app;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javafx.scene.Node;
import ph.txtdis.dto.BillableDetail;
import ph.txtdis.exception.NotFoundException;
import ph.txtdis.fx.control.AppButton;
import ph.txtdis.fx.control.AppCombo;
import ph.txtdis.fx.control.AppField;
import ph.txtdis.fx.control.LocalDatePicker;
import ph.txtdis.fx.dialog.NewCustomerDialog;
import ph.txtdis.fx.table.OrderConfirmationTable;
import ph.txtdis.service.CokeBillableService;
import ph.txtdis.type.Type;

@Scope("prototype")
@Component("orderConfirmationApp")
public class OrderConfirmationAppImpl extends AbstractBillableApp<CokeBillableService, OrderConfirmationTable, Long> {

	private static final String ORDER_NO = "Order No. ";

	private static final String ORDER_DATE = "Order Date ";

	private static final String CUSTOMER_NO = "Customer No. ";

	private static final String FOR = "\nfor ";

	private static final String FORMAT_IS = "Format is: ";

	private static final String ROUTE = "E3CX";

	private static final String SHIPMENT_ID = "12345678";

	private static final String CUSTOMER_ID = SHIPMENT_ID + "9";

	private static final String SHIPMENT_NO = "Shipment No. ";

	@Autowired
	private AppCombo<String> routeCombo, typeCombo;

	@Autowired
	private AppField<String> referenceOrderNoInput;

	@Autowired
	private LocalDatePicker dueDatePicker;

	@Autowired
	private NewCustomerDialog newCustomerDialog;

	@Autowired
	private TotaledTableApp<BillableDetail> totaledTableApp;

	@Override
	protected List<AppButton> addButtons() {
		List<AppButton> b = new ArrayList<>(super.addButtons());
		b.remove(decisionButton);
		return b;
	}

	@Override
	protected void addressAndOrRemarksGridLines() {
		if (service.isAnOrderConfirmation())
			;
		// TODO addRemarks();
	}

	@Override
	protected void customerGridLine() {
		if (service.isAnOrderConfirmation())
			customerWithDueDateGridLine();
	}

	@Override
	protected Node dueDateNode() {
		if (service.isAnOrderConfirmation())
			return stackPane(super.dueDateNode(), dueDatePicker);
		return super.dueDateNode();
	}

	@Override
	protected List<Node> mainVerticalPaneNodes() {
		// TODO Auto-generated method stub
		buildDisplays();
		List<Node> l = new ArrayList<>(Arrays.asList(gridPane()));
		if (service.isADeliveryListOrAnOrderConfirmation())
			l.add(totaledTableApp.addNoSubHeadTablePane(table));
		else
			l.add(tablePane());
		// TODO l.add(auditPane());
		return l;
	}

	// TODO @Override
	protected void openByIdDialog() {
		openByIdDialog.idPrompt(idPrompt()).header(openByIdDialogHeader()).prompt(openByIdDialogPrompt()).addParent(this)
				.start();
	}

	private String idPrompt() {
		if (service.isADeliveryList())
			return SHIPMENT_NO + "+ Route";
		if (service.isAnOrderConfirmation())
			return CUSTOMER_NO + "+ " + ORDER_DATE + "+ " + ORDER_NO;
		return "";
	}

	@Override
	protected String openByIdDialogPrompt() {
		if (service.isADeliveryList())
			return FORMAT_IS + SHIPMENT_ID + ROUTE + FOR + SHIPMENT_NO + SHIPMENT_ID + " & Route " + ROUTE;
		if (service.isALoadManifest())
			return "Enter " + SHIPMENT_NO;
		if (service.isAnOrderConfirmation()) // TODO || service.isAReceiving())
			return FORMAT_IS + CUSTOMER_ID + "-20080808/1" + FOR + CUSTOMER_NO + CUSTOMER_ID + ", " + ORDER_DATE
					+ "8/8/08 & " + ORDER_NO + "1";
		return "";
	}

	@Override
	protected Node orderDateNode() {
		if (service.isADeliveryListOrAnOrderConfirmationOrALoadManifest())
			return stackPane(orderDateDisplay, orderDatePicker);
		return super.orderDateNode();
	}

	// TODO @Override
	protected void referenceGridNodes() {
		if (service.isADeliveryListOrAnOrderConfirmation())
			addRouteGridNodes();
		if (service.isAnOrderConfirmation())
			addTypeGridNodes();
	}

	private void addRouteGridNodes() {
		// TODO gridPane.add(label.field(service.getReferenceName()), 5, 0);
		gridPane.add(routeCombo, 6, 0);
	}

	private void addTypeGridNodes() {
		gridPane.add(label.field("Type"), 7, 0);
		gridPane.add(typeCombo, 8, 0);
	}

	@Override
	protected Node referenceNode() {
		return referenceOrderNoInput.prompt("12345678-20080808/1").build(Type.TEXT);
	}

	@Override
	public void refresh() {
		super.refresh();
		routeCombo.items(service.listRoutes());
		typeCombo.items(service.listTypes());
		dueDatePicker.setValue(service.getDueDate());
		referenceOrderNoInput.setValue(service.getReferenceOrderNo());
		updateSummaries();
	}

	@Override
	protected void reset() {
		super.reset();
		if (service.isADeliveryListOrAnOrderConfirmationOrALoadManifest())
			orderDatePicker.requestFocus();
		else
			referenceOrderNoInput.requestFocus();
	}

	// TODO @Override
	protected void rrGridNodes() {
		// TODO receivingReferenceNodes(3);
	}

	@Override
	protected void setBindings() {
		super.setBindings();
		// TODO routeCombo.disableIf(idNoInput.isEmpty());
		typeCombo.disableIf(routeCombo.isEmpty());
		dueDatePicker.disableIf(routeCombo.isEmpty());
		dueDatePicker.visibleProperty().bind(isPosted().not());
		dueDateDisplay.visibleProperty().bind(dueDatePicker.visibleProperty().not());
		// TODO customerIdInput.disableProperty().unbind();
		// TODO customerIdInput.disableIf(dueDatePicker.isEmpty().or(isPosted()).or(customerNameDisplay.isNotEmpty()));
	}

	@Override
	protected void setButtonBindings() {
		// TODO super.setButtonBindings();
		// TODO customerSearchButton.visibleProperty().unbind();
		// TODO customerSearchButton.setVisible(false);
		saveButton.disableProperty().unbind();
		saveButton.disableIf(isPosted().or(table.isEmpty()));
	}

	@Override
	public void setFocus() {
		newButton.requestFocus();
	}

	@Override
	protected void setInputFieldBindings() {
		super.setInputFieldBindings();
		remarksDisplay.editableIf(createdOnDisplay.isEmpty()); // TODO .and(isAnOrderConfirmation()));
	}

	@Override
	protected void setListeners() {
		super.setListeners();
		dueDatePicker.setOnAction(e -> service.setDueDate(dueDatePicker.getValue()));
		referenceOrderNoInput.setOnAction(e -> updateUponReferenceIdValidation());
		routeCombo.setOnAction(e -> service.setRoute(routeCombo.getValue()));
		typeCombo.setOnAction(e -> service.setType(typeCombo.getValue()));

	}

	private void updateUponReferenceIdValidation() {
		String id = referenceOrderNoInput.getValue();
		if (service.isNew() && id != null && !id.trim().isEmpty())
			try {
				service.updateUponReferenceOrderNoValidation(id);
			} catch (Exception e) {
				handleError(e, referenceOrderNoInput);
			} finally {
				refresh();
			}
	}

	@Override
	protected void setTableBindings() {
		// TODO table.disableIf(when(isAnOrderReturn()).then(orderDateDisplay.isEmpty()).otherwise(customerNameDisplay.isEmpty()));
	}

	@Override
	public void start() {
		if (service.isADeliveryListOrAnOrderConfirmation())
			totaledTableApp.addTotalDisplays(1);
		super.start();
	}

	// TODO @Override
	protected void updateSummaries() {
		if (service.isADeliveryListOrAnOrderConfirmation())
			totaledTableApp.refresh(service);
	}

	// TODO @Override
	protected void updateUponCustomerValidation() {
		Long id = null; // TODO customerIdInput.getValue();
		if (// TODO isNew() && 
		id != 0)
			try {
				service.updateUponCustomerVendorIdValidation(id);
			} catch (NotFoundException e) {
				showCreateNewOutletOrExitDialog(e, id);
			} catch (Exception e) {
				// TODO handleError(e, customerIdInput);
			} finally {
				// TODO refreshCustomerRelatedInputs();
				// TODO 	setFocusAfterCustomerValidation();
			}
	}

	private void showCreateNewOutletOrExitDialog(Exception x, Long id) {
		dialog.showOption(x.getMessage(), "Create", "Exit");
		dialog.setOnOptionSelection(e -> openAddNewOutletDialog(id));
		// TODO dialog.setOnDefaultSelection(e -> resetCustomerData());
		dialog.addParent(this).start();
	}

	private void openAddNewOutletDialog(Long id) {
		newCustomerDialog.vendorId(id).addParent(this).start();
		dialog.close();
		// TODO service.setCustomer(newCustomerDialog.getCustomer());
		// TODO service.setCustomerRelatedData();
	}

	// TODO  @Override
	protected void updateUponDateValidation(LocalDate d) {
		// TODO if (isNew() && d != null && (service.isADeliveryListOrAnOrderConfirmationOrALoadManifest())) setOrderDateUponValidation(d);
	}

	@Override
	protected void topGridLine() {
		// TODO Auto-generated method stub
	}
}
