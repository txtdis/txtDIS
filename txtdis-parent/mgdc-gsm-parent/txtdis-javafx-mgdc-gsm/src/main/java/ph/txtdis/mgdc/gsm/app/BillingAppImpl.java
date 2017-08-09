package ph.txtdis.mgdc.gsm.app;

import javafx.beans.binding.BooleanBinding;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.scene.Node;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import ph.txtdis.app.App;
import ph.txtdis.dto.Billable;
import ph.txtdis.fx.control.AppButton;
import ph.txtdis.fx.control.AppFieldImpl;
import ph.txtdis.mgdc.app.MultiTypeBillingApp;
import ph.txtdis.mgdc.fx.table.BeverageBillingTable;
import ph.txtdis.mgdc.gsm.fx.dialog.InvoiceNoEditorDialog;
import ph.txtdis.mgdc.gsm.fx.dialog.InvoiceValueAdjustmentDialog;
import ph.txtdis.mgdc.gsm.service.GsmBillingService;
import ph.txtdis.type.ModuleType;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static java.util.Arrays.asList;
import static ph.txtdis.type.Type.*;

@Scope("prototype")
@Component("billingApp")
public class BillingAppImpl //
	extends AbstractBillableApp<GsmBillingService, BeverageBillingTable, String> //
	implements MultiTypeBillingApp {

	@Autowired
	private AppButton adjustButton, editButton;

	@Autowired
	private AppFieldImpl<BigDecimal> grossDisplay, adjustmentDisplay;

	@Autowired
	private AppFieldImpl<Long> idNoInput;

	@Autowired
	private AppFieldImpl<String> idPrefixInput, idSuffixInput;

	@Autowired
	private InvoiceNoEditorDialog invoiceNoEditorDialog;

	@Autowired
	private InvoiceValueAdjustmentDialog invoiceValueAdjustmentDialog;

	private BooleanProperty canEditInvoiceNo;

	private ModuleType type;

	@Override
	protected List<AppButton> addButtons() {
		List<AppButton> b = new ArrayList<>(super.addButtons());
		b.add(4, adjustButton);
		if (service.isAnInvoice())
			b.add(6, editButton);
		return b;
	}

	@Override
	protected void buildButttons() {
		super.buildButttons();
		adjustButton.icon("adjustment").tooltip("Adjust value...").build();
		editButton.icon("edit").tooltip("Edit S/I No...").build();
	}

	@Override
	protected void buildFields() {
		super.buildFields();
		buildIdNoInput();
		idPrefixInput.readOnly().width(70).build(CODE);
		idSuffixInput.width(40).build(CODE);
		grossDisplay.readOnly().build(CURRENCY);
		adjustmentDisplay.readOnly().build(CURRENCY);
	}

	private void buildIdNoInput() {
		if (service.isAnInvoice())
			idNoInput.build(ID);
		else
			idNoInput.width(180).build(ID);
	}

	@Override
	protected List<Node> mainVerticalPaneNodes() {
		List<Node> l = new ArrayList<>(super.mainVerticalPaneNodes());
		l.add(totalPane());
		l.add(paymentPane());
		l.add(bookingAndReceivingPane());
		l.add(billingAndDecisionPane());
		return l;
	}

	private Node totalPane() {
		return service.isAnInvoice() ? vatAndTotalPane() : totalOnlyPane();
	}

	private Node paymentPane() {
		return pane.centeredHorizontal(paymentAndBalanceNodes());
	}

	private Node bookingAndReceivingPane() {
		List<Node> l = new ArrayList<>(creationNodes());
		l.addAll(receivingNodes());
		return pane.centeredHorizontal(l);
	}

	private Node billingAndDecisionPane() {
		List<Node> l = new ArrayList<>(billingNodes(service.getBillingPrompt()));
		l.addAll(decisionNodes());
		return pane.centeredHorizontal(l);
	}

	private Node totalOnlyPane() {
		List<Node> l = new ArrayList<>(adjustmentNodes());
		l.addAll(totalNodes());
		return pane.centeredHorizontal(l);
	}

	private List<Node> paymentAndBalanceNodes() {
		return asList( //
			label.name("Payment"), paymentCombo, //
			label.name("Balance"), balanceDisplay);
	}

	private List<Node> adjustmentNodes() {
		return asList( //
			label.name("Gross"), grossDisplay, //
			label.name("Adjustment"), adjustmentDisplay);
	}

	@Override
	protected Node orderDateNode() {
		return orderDateDisplayOnly();
	}

	@Override
	protected void refreshSummaryPane() {
		super.refreshSummaryPane();
		refreshVatNodes();
		refreshPaymentNodes();
	}

	private void refreshVatNodes() {
		grossDisplay.setValue(service.getGrossValue());
		adjustmentDisplay.setValue(service.getAdjustment());
		vatableDisplay.setValue(service.getVatable());
		vatDisplay.setValue(service.getVat());
	}

	private void refreshPaymentNodes() {
		paymentCombo.itemsSelectingFirst(service.getPayments());
		balanceDisplay.setValue(service.getBalance());
	}

	@Override
	protected void renew() {
		super.renew();
		if (service.findUncorrectedInvalidBilling()) {
			messageDialog.showError("Correct invalid S/I & D/R's first").addParent(this).start();
			refresh();
			table.requestFocus();
		}
		else if (service.isAnInvoice()) {
			idPrefixInput.enable();
			idPrefixInput.requestFocus();
		}
		else
			referenceIdInput.requestFocus();
	}

	@Override
	public void refresh() {
		super.refresh();
		idNoInput.setValue(service.getNumId());
		idPrefixInput.setValue(service.getPrefix());
		idSuffixInput.setValue(service.getSuffix());
		canEditInvoiceNo.set(service.canEditInvoiceNo());
	}

	@Override
	protected BooleanBinding saveButtonDisableBinding() {
		BooleanBinding b = isPosted() //
			.or(table.isEmpty());
		return !service.isAnInvoice() ? b : b.or(idNoInput.isEmpty());
	}

	@Override
	protected void setBooleanProperties() {
		canEditInvoiceNo = new SimpleBooleanProperty(service.canEditInvoiceNo());
	}

	@Override
	protected void setButtonBindings() {
		super.setButtonBindings();
		adjustButton.disableIf(isPosted() //
			.or(table.isEmpty()));
		editButton.disableIf(isNew() //
			.or(canEditInvoiceNo.not()));
	}

	@Override
	protected BooleanBinding isNew() {
		return billedOnDisplay.isEmpty();
	}

	@Override
	protected void secondGridLine() {
		customerWithDueDateGridLine();
	}

	@Override
	protected void setInputFieldBindings() {
		super.setInputFieldBindings();
		idPrefixInput.disableIf(isPosted());//
		setIdNoInputBindings();
		setIdSuffixInputBindings();
		setCustomerBoxBindings();
	}

	private void setIdNoInputBindings() {
		if (service.isAnInvoice())
			idNoInput.disableIf(isPosted());
		else
			idNoInput.disable();
	}

	private void setIdSuffixInputBindings() {
		idSuffixInput.disableIf(isPosted() //
			.or(idNoInput.isEmpty()));
	}

	private void setCustomerBoxBindings() {
		customerBox.disableIdInputIf(isPosted());
		if (!service.isNew())
			customerBox.hideSearchButton();
	}

	@Override
	protected void setListeners() {
		super.setListeners();
		idSuffixInput.onAction(
			e -> updateUponOrderNoValidation(idPrefixInput.getValue(), idNoInput.getValue(), idSuffixInput.getValue()));
		referenceIdInput.onAction(e -> updateUponReferenceIdValidation(referenceIdInput.getValue()));
		customerBox.onAction(e -> updateUponCustomerValidation(customerBox.getId()));
		adjustButton.onAction(e -> adjustTotalValue());
		editButton.onAction(e -> editInvoiceNo());
	}

	private void updateUponOrderNoValidation(String prefix, Long id, String suffix) {
		if (service.isNew() || !saveButton.isDisabled())
			try {
				service.updateUponOrderNoValidation(prefix, id, suffix);
			} catch (Exception e) {
				handleError(e, idPrefixInput, idNoInput, idSuffixInput);
			}
	}

	private void updateUponReferenceIdValidation(Long id) {
		if (service.isNew() && id != 0)
			try {
				service.updateUponReferenceIdValidation(id);
			} catch (Exception e) {
				handleError(e, referenceIdInput);
			} finally {
				setFocusAfterReferenceIdValidation();
			}
	}

	private void updateUponCustomerValidation(Long id) {
		if (service.isNew() && id != null && id != 0)
			try {
				service.updateUponCustomerIdValidation(id);
			} catch (Exception e) {
				customerBox.handleError(e);
			} finally {
				refresh();
				customerBox.setFocusAfterCustomerValidation(table);
			}
	}

	private void adjustTotalValue() {
		invoiceValueAdjustmentDialog.addParent(this).start();
		refresh();
	}

	private void editInvoiceNo() {
		invoiceNoEditorDialog.addParent(this).start();
		refresh();
	}

	private void setFocusAfterReferenceIdValidation() {
		if (referenceIdInput.isEmpty().get())
			referenceIdInput.requestFocus();
		else if (service.isAppendable())
			customerBox.requestFocus();
		else
			saveButton.requestFocus();
	}

	@Override
	protected void setReferenceIdBindings() {
		if (service.isAnInvoice())
			referenceIdInput.disableIf(isPosted() //
				.or(idSuffixInput.disabledProperty()));
		else
			super.setReferenceIdBindings();
	}

	@Override
	public void show(Billable b) {
		service.setUncorrectedInvalidBilling(b);
		refresh();
	}

	@Override
	protected void showAuditDialogToValidateOrder() {
		Billable b = service.findUnvalidatedCorrectedBilling();
		if (service.isAuditor() && b != null && !b.getId().equals(service.getId()))
			validateCorrectedBilling(b);
		else
			super.showAuditDialogToValidateOrder();
	}

	private void validateCorrectedBilling(Billable b) {
		showCorrectedBillingMustBeValidatedFirstMessage(b.getOrderNo());
		actOn(b.getId().toString(), "");
	}

	private void showCorrectedBillingMustBeValidatedFirstMessage(String orderNo) {
		String msg = "Validate corrected\nS/I/(D/R) No. " + orderNo + "\nfirst, to proceed.";
		messageDialog.showError(msg).addParent(this).start();
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
		if (!service.isAnInvoice())
			return pane.horizontal(idNoInput);
		return pane.horizontal(idPrefixInput, idNoInput, idSuffixInput);
	}

	@Override
	public ModuleType type() {
		return type;
	}

	@Override
	public App type(ModuleType type) {
		this.type = type;
		return this;
	}

	@Override
	protected List<Node> vatAndTotalNodes() {
		List<Node> l = new ArrayList<>(adjustmentNodes());
		l.addAll(super.vatAndTotalNodes());
		return l;
	}
}
