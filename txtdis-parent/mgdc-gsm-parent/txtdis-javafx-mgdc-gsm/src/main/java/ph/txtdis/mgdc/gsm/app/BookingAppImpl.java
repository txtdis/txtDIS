package ph.txtdis.mgdc.gsm.app;

import static org.apache.log4j.Logger.getLogger;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javafx.beans.binding.BooleanBinding;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.scene.Node;
import javafx.scene.layout.HBox;
import ph.txtdis.app.StartableApp;
import ph.txtdis.dto.Billable;
import ph.txtdis.exception.BadCreditException;
import ph.txtdis.exception.ExceededCreditLimitException;
import ph.txtdis.fx.control.AppButtonImpl;
import ph.txtdis.fx.control.AppCombo;
import ph.txtdis.info.Information;
import ph.txtdis.mgdc.app.MultiTypeBillingApp;
import ph.txtdis.mgdc.app.SalesOrderApp;
import ph.txtdis.mgdc.gsm.fx.table.SalesOrderTable;
import ph.txtdis.mgdc.gsm.service.GsmBookingService;
import ph.txtdis.type.ModuleType;
import ph.txtdis.type.Type;

@Scope("prototype")
@Component("bookingApp")
public class BookingAppImpl //
		extends AbstractBillableApp<GsmBookingService, SalesOrderTable, Long> //
		implements SalesOrderApp {

	private static Logger logger = getLogger(BookingAppImpl.class);

	@Autowired
	private AppButtonImpl changeDetailsButton, overrideButton;

	@Autowired
	private AppCombo<String> exTruckCombo;

	@Autowired
	private MultiTypeBillingApp billingApp;

	@Autowired
	private RouteItineraryApp routeItineraryApp;

	private BooleanProperty canChangeDetails, detailsChanged, invalidSalesOrderCanBeOverriden;

	private ModuleType type;

	@Override
	protected List<AppButtonImpl> addButtons() {
		List<AppButtonImpl> b = new ArrayList<>(super.addButtons());
		if (!isExTruck()) {
			b.add(overrideButton);
			b.add(changeDetailsButton);
		} else
			b.add(routeItineraryApp.addButton(this, service));
		return b;
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

	private boolean isExTruck() {
		return type == ModuleType.EX_TRUCK;
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
		dialog.showError("Correct invalid S/I & D/R's first").addParent(this).start();
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
		return table.isEmpty() //
				.or(changeDetailsButton.disabledProperty().not()) //
				.or(isPosted() //
						.and(canChangeDetails.not() //
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
		changeDetailsButton.disableIf( //
				canChangeDetails.not() //
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
			remarksDisplay.editableIf(isNew() //
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
		dialog.showOption(badCreditMsg, "Book—get approval later", "Exit");
		dialog.setOnOptionSelection(e -> setCustomerDataAndTemporarilyInvalidateAwaitingApproval(badCreditMsg));
		dialog.setOnDefaultSelection(e -> resetCustomerData());
		dialog.addParent(this).start();
	}

	private void setCustomerDataAndTemporarilyInvalidateAwaitingApproval(String badCreditMessage) {
		service.setCustomerRelatedData();
		service.invalidateAwaitingApproval(badCreditMessage);
		decisionNeededApp.refresh(service);
		dialog.close();
	}

	private void resetCustomerData() {
		service.reset();
		dialog.close();
	}

	private void overrideInvalidation() {
		try {
			service.overrideInvalidation();
		} catch (Information i) {
			dialog.show(i).addParent(this).start();
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
	protected HBox trackedPane() {
		List<Node> l = new ArrayList<>(super.trackedPane().getChildren());
		l.addAll(decisionNodes());
		return box.forHorizontalPane(l);
	}

	@Override
	public StartableApp type(ModuleType type) {
		this.type = type;
		return this;
	}

	@Override
	public ModuleType type() {
		return type;
	}
}
