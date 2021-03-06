package ph.txtdis.mgdc.gsm.app;

import javafx.beans.binding.BooleanBinding;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.scene.Node;
import javafx.scene.layout.HBox;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import ph.txtdis.app.App;
import ph.txtdis.dto.Billable;
import ph.txtdis.exception.BadCreditException;
import ph.txtdis.exception.ExceededCreditLimitException;
import ph.txtdis.fx.control.AppButton;
import ph.txtdis.fx.control.AppCombo;
import ph.txtdis.fx.dialog.MessageDialog;
import ph.txtdis.info.Information;
import ph.txtdis.mgdc.app.MultiTypeBillingApp;
import ph.txtdis.mgdc.app.SalesOrderApp;
import ph.txtdis.mgdc.gsm.fx.table.SalesOrderTable;
import ph.txtdis.mgdc.gsm.service.GsmBookingService;
import ph.txtdis.type.ModuleType;
import ph.txtdis.type.Type;

import java.util.ArrayList;
import java.util.List;

import static org.apache.log4j.Logger.getLogger;

@Scope("prototype")
@Component("bookingApp")
public class BookingAppImpl
	extends AbstractBillableApp<GsmBookingService, SalesOrderTable, Long>
	implements SalesOrderApp {

	private static Logger logger = getLogger(BookingAppImpl.class);

	@Autowired
	private AppButton changeDetailsButton, overrideButton;

	@Autowired
	private AppCombo<String> exTruckCombo;

	@Autowired
	private MultiTypeBillingApp billingApp;

	@Autowired
	private RouteItineraryApp routeItineraryApp;

	private BooleanProperty canChangeDetails, detailsChanged, invalidSalesOrderCanBeOverriden;

	private ModuleType type;

	private MessageDialog messageDialog;

	@Override
	protected List<AppButton> addButtons() {
		List<AppButton> b = new ArrayList<>(super.addButtons());
		if (!isExTruck()) {
			b.add(overrideButton);
			b.add(changeDetailsButton);
		}
		else
			b.add(routeItineraryApp.addButton(this, service));
		return b;
	}

	private boolean isExTruck() {
		return type == ModuleType.EX_TRUCK;
	}

	@Override
	protected void addressAndOrRemarksGridLine() {
		if (isExTruck())
			remarksGridLineAtRowSpanning(1, 8);
		else
			super.addressAndOrRemarksGridLine();
	}

	@Override
	protected void buildButttons() {
		super.buildButttons();
		overrideButton.icon("override").tooltip("Override\nS/O invalidation").build();
		changeDetailsButton.icon("edit").tooltip("Change Details...").build();
	}

	@Override
	protected void secondGridLine() {
		if (!isExTruck())
			customerWithDueDateGridLine();
	}

	@Override
	protected List<Node> mainVerticalPaneNodes() {
		List<Node> l = new ArrayList<>(super.mainVerticalPaneNodes());
		if (!isExTruck())
			l.add(vatAndTotalPane());
		l.add(trackedPane());
		return l;
	}

	@Override
	protected HBox trackedPane() {
		List<Node> l = new ArrayList<>(super.trackedPane().getChildren());
		l.addAll(decisionNodes());
		return pane.centeredHorizontal(l);
	}

	@Override
	protected Node orderDateNode() {
		return orderDateStackPane();
	}

	@Override
	public void refresh() {
		super.refresh();
		decisionNeededApp.refresh(service);
		invalidSalesOrderCanBeOverriden.set(service.canInvalidSalesOrderBeOverriden());
		orderNoDisplay.setValue(service.getOrderNo());
		canChangeDetails.set(service.canChangeDetails());
		detailsChanged.set(service.detailsChanged());
		refreshSummaryPane();
		if (isExTruck())
			exTruckCombo.items(service.listExTrucksWithoutLoadOrders());
	}

	@Override
	protected void refreshSummaryPane() {
		super.refreshSummaryPane();
		vatableDisplay.setValue(service.getVatable());
		vatDisplay.setValue(service.getVat());
	}

	@Override
	protected void renew() {
		super.renew();
		Billable b = service.findUncorrectedInvalidBilling();
		if (b != null)
			correctInvalidBilling(b);
		else
			startByInputtingDate();
	}

	private void correctInvalidBilling(Billable b) {
		messageDialog().showError("Correct invalid S/I & D/R's first").addParent(this).start();
		close();
		billingApp.type(b.type()).start();
		billingApp.show(b);
	}

	private void startByInputtingDate() {
		orderDatePicker.enable();
		orderDatePicker.requestFocus();
	}

	@Override
	protected BooleanBinding saveButtonDisableBinding() {
		return table.isEmpty()
			.or(changeDetailsButton.disabledProperty().not())
			.or(isPosted()
				.and(canChangeDetails.not()
					.and(detailsChanged.not())));
	}

	@Override
	protected void setBooleanProperties() {
		invalidSalesOrderCanBeOverriden = new SimpleBooleanProperty(service.canInvalidSalesOrderBeOverriden());
		canChangeDetails = new SimpleBooleanProperty(service.canChangeDetails());
		detailsChanged = new SimpleBooleanProperty(service.detailsChanged());
	}

	@Override
	protected void setButtonBindings() {
		super.setButtonBindings();
		overrideButton.disableIf(invalidSalesOrderCanBeOverriden.not());
		decisionButton.disable();
		changeDetailsButton.disableIf(
			canChangeDetails.not()
				.or(detailsChanged));
	}

	@Override
	protected void setInputFieldBindings() {
		customerBox.disableIdInputIf(isPosted());
		customerBox.setSearchButtonVisibleIfNot(isPosted());
		referenceIdInput.setDisable(true);
		orderDatePicker.disable();
		exTruckCombo.disableIf(orderDatePicker.disabled());
		if (isExTruck())
			remarksDisplay.editableIf(isNew()
				.and(exTruckCombo.isNotEmpty()));
	}

	@Override
	protected void setListeners() {
		super.setListeners();
		customerBox.onAction(e -> updateUponCustomerValidation());
		overrideButton.onAction(e -> overrideInvalidation());
		exTruckCombo.onAction(e -> setExTruckAsCustomerUponValidation(exTruckCombo.getValue()));
		orderDatePicker.onAction(e -> service.setOrderDate(orderDatePicker.getValue()));
		changeDetailsButton.onAction(e -> clearTableMakingItAppendableAndUpdateDiscountAndPriceData());
	}

	private void updateUponCustomerValidation() {
		Long id = customerBox.getId();
		if (service.isNew() && id != null && id != 0)
			try {
				service.updateUponCustomerIdValidation(id);
			} catch (BadCreditException | ExceededCreditLimitException e) {
				showProceedAndGetApprovalLaterOrExitDialog(e.getMessage());
			} catch (Exception e) {
				customerBox.handleError(e);
			} finally {
				refresh();
				customerBox.setFocusAfterCustomerValidation(table);
			}
	}

	private void showProceedAndGetApprovalLaterOrExitDialog(String badCreditMsg) {
		messageDialog = messageDialog();
		messageDialog.showOption(badCreditMsg, "Book—get approval later", "Exit")
			.setOnOptionSelection(e -> setCustomerDataAndTemporarilyInvalidateAwaitingApproval(badCreditMsg))
			.setOnDefaultSelection(e -> resetCustomerData())
			.addParent(this).start();
	}

	private void setCustomerDataAndTemporarilyInvalidateAwaitingApproval(String badCreditMessage) {
		service.setCustomerRelatedData();
		service.invalidateAwaitingApproval(badCreditMessage);
		decisionNeededApp.refresh(service);
		messageDialog.close();
	}

	private void resetCustomerData() {
		service.reset();
		messageDialog.close();
	}

	private void overrideInvalidation() {
		try {
			service.overrideInvalidation();
		} catch (Information i) {
			showInfoDialog(i);
		} catch (Exception e) {
			showErrorDialog(e);
		} finally {
			decisionNeededApp.refresh(service);
			refresh();
			invalidSalesOrderCanBeOverriden.set(false);
		}
	}

	private void setExTruckAsCustomerUponValidation(String exTruck) {
		logger.info("\n    ExTruckComboValue = " + exTruckCombo.getValue());
		if (exTruck != null && !exTruck.isEmpty() && service.isNew())
			try {
				service.setExTruckAsCustomerIfExTruckPreviousTransactionsAreComplete(exTruck);
				remarksDisplay.requestFocus();
			} catch (Exception e) {
				showErrorDialog(e);
				service.reset();
				refresh();
			}
	}

	private void clearTableMakingItAppendableAndUpdateDiscountAndPriceData() {
		service.resetDetailsAndUpdateDiscountsAndPrices();
		refresh();
		table.requestFocus();
	}

	@Override
	protected void setTableBindings() {
		if (!isExTruck())
			super.setTableBindings();
		else
			table.disableIf(exTruckCombo.isEmpty());
	}

	@Override
	public void start() {
		service.setType(type);
		super.start();
	}

	@Override
	protected void topGridLine() {
		dateGridNodes();
		idGridNodes();
		referenceGridNodes(service.getReferencePrompt(), 5);
		receivingGridNodes(service.getReceivingPrompt(), 7);
	}

	private void idGridNodes() {
		gridPane.add(label.field(service.getIdPrompt()), 3, 0);
		gridPane.add(idBox(), 4, 0);
	}

	private Node idBox() {
		if (!isExTruck())
			return orderNoDisplay.readOnly().width(180).build(Type.TEXT);
		return exTruckCombo.readOnlyOfWidth(140);
	}

	@Override
	public App type(ModuleType type) {
		this.type = type;
		return this;
	}

	@Override
	public ModuleType type() {
		return type;
	}
}
