package ph.txtdis.app;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.scene.Node;
import ph.txtdis.exception.BadCreditException;
import ph.txtdis.exception.ExceededCreditLimitException;
import ph.txtdis.fx.control.AppButton;
import ph.txtdis.fx.control.AppField;
import ph.txtdis.fx.table.BillableTable;
import ph.txtdis.info.Information;
import ph.txtdis.service.BookingService;
import ph.txtdis.type.Type;

public abstract class AbstractBookingApp<AS extends BookingService, AT extends BillableTable> // 
		extends AbstractBillableApp<AS, AT, Long> implements BookingApp {

	@Autowired
	private AppField<String> orderNoDisplay;

	@Autowired
	protected AppButton overrideButton;

	private BooleanProperty invalidSalesOrderCanBeOverriden;

	@Override
	protected List<AppButton> addButtons() {
		List<AppButton> b = new ArrayList<>(super.addButtons());
		b.add(overrideButton);
		return b;
	}

	@Override
	protected void addressAndOrRemarksGridLines() {
		addressGridNodes();
		addRemarksAtLine(3, 8);
	}

	@Override
	protected void buildButttons() {
		super.buildButttons();
		overrideButton.icon("override").tooltip("Override\nS/O invalidation").build();
	}

	@Override
	protected void customerGridLine() {
		customerWithDueDateGridLine();
	}

	@Override
	public void refresh() {
		super.refresh();
		decisionNeededApp.refresh(service);
		invalidSalesOrderCanBeOverriden.set(service.canInvalidSalesOrderBeOverriden());
		orderNoDisplay.setValue(service.getOrderNo());
		refreshSummaryPane();
	}

	private void refreshSummaryPane() {
		totalDisplay.setValue(service.getTotalValue());
		refreshVatNodes();
	}

	private void refreshVatNodes() {
		vatableDisplay.setValue(service.getVatable());
		vatDisplay.setValue(service.getVat());
	}

	@Override
	protected void reset() {
		super.reset();
		customerBox.requestFocus();
	}

	@Override
	protected void setBooleanProperties() {
		super.setBooleanProperties();
		invalidSalesOrderCanBeOverriden = new SimpleBooleanProperty(service.canInvalidSalesOrderBeOverriden());
	}

	@Override
	protected void setButtonBindings() {
		customerBox.disableIdInputIf(isPosted());
		customerBox.setSearchButtonVisibleIfNot(isPosted());
		decisionButton.disableIf(notPosted());
		saveButton.disableIf(table.isEmpty().or(isPosted()));
		overrideButton.disableIf(invalidSalesOrderCanBeOverriden.not());
	}

	@Override
	protected void setInputFieldBindings() {
		customerBox.disableIdInputIf(isPosted());
		customerBox.setSearchButtonVisibleIfNot(isPosted());
		orderDateDisplay.visibleProperty()
				.bind(isPosted() //
						.or(orderDatePicker.disabledProperty()) //
						.or(orderDatePicker.visibleProperty().not()));
		orderDatePicker.visibleProperty().bind(isPosted().not());
		referenceIdInput.setDisable(true);
	}

	@Override
	protected void setListeners() {
		super.setListeners();
		customerBox.setOnAction(e -> updateUponCustomerValidation());
		decisionNeededApp.setDecisionButtonOnAction(e -> showAuditDialogToValidateOrder());
		overrideButton.setOnAction(e -> overrideInvalidation());
		table.setOnItemChange(e -> updateSummaries());
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
		dialog.showOption(badCreditMsg + "—proceed how?", "Book—get approval later", "Exit");
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

	private void showAuditDialogToValidateOrder() {
		decisionNeededApp.showDecisionDialogForValidation(this, service);
		refresh();
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

	private void updateSummaries() {
		service.updateSummaries(table.getItems());
		refreshSummaryPane();
	}

	@Override
	protected void topGridLine() {
		dateGridNodes();
		idGridNodes();
		referenceGridNodes(service.getReferencePrompt(), 5);
		receivingGridNodes(service.getReceivingPrompt(), 7);
	}

	protected void idGridNodes() {
		gridPane.add(label.field(service.getIdPrompt()), 3, 0);
		gridPane.add(idBox(), 4, 0);
	}

	protected Node idBox() {
		return orderNoDisplay.readOnly().width(180).build(Type.TEXT);
	}
}
