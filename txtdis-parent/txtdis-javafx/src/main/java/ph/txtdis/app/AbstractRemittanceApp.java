package ph.txtdis.app;

import javafx.beans.binding.BooleanBinding;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.scene.Node;
import javafx.scene.layout.HBox;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Lookup;
import ph.txtdis.dto.Remittance;
import ph.txtdis.fx.control.AppButton;
import ph.txtdis.fx.control.AppCombo;
import ph.txtdis.fx.control.AppFieldImpl;
import ph.txtdis.fx.dialog.CheckSearchDialog;
import ph.txtdis.fx.dialog.DepositDialog;
import ph.txtdis.fx.pane.AppGridPane;
import ph.txtdis.service.RemittanceService;
import ph.txtdis.type.PaymentType;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

import static java.util.Arrays.asList;
import static ph.txtdis.type.PaymentType.CASH;
import static ph.txtdis.type.PaymentType.CHECK;
import static ph.txtdis.type.Type.*;

public abstract class AbstractRemittanceApp<AS extends RemittanceService>
	extends AbstractRemarkedKeyedApp<AS, Remittance, Long, Long>
	implements RemittanceApp {

	static final String CANCELLED = "CANCELLED";

	@Autowired
	protected AppCombo<String> draweeBankCombo, receivedFromCombo;

	@Autowired
	protected AppCombo<PaymentType> paymentCombo;

	@Autowired
	protected AppFieldImpl<BigDecimal> amountInput;

	@Autowired
	protected AppFieldImpl<Long> checkIdInput;

	@Autowired
	protected AppFieldImpl<String> depositedToDisplay, depositEncodedByDisplay;

	@Autowired
	protected AppFieldImpl<ZonedDateTime> depositedOnDisplay, depositEncodedOnDisplay;

	protected BooleanProperty canPostPaymentData;

	private AppButton checkSearchButton, depositButton;

	@Override
	protected List<AppButton> addButtons() {
		List<AppButton> b = super.addButtons();
		b.add(decisionButton = decisionNeededApp.addDecisionButton());
		b.add(depositButton = button.icon("deposit").tooltip("Enter deposit\ndata").build());
		b.add(checkSearchButton = button.icon("checkSearch").tooltip("Find a check").build());
		return b;
	}

	protected void depositAndRemarksNodesStartingAtLineNo(int line) {
		gridPane.add(label.field("Deposited to"), 0, line);
		gridPane.add(depositGridNode(), 1, line, 5, 1);
		gridPane.add(label.field("Remarks"), 0, ++line);
		gridPane.add(remarksDisplay.build(), 1, line, 5, 2);
	}

	private Node depositGridNode() {
		return pane.forGridGroup(
			depositedToDisplay.readOnly().width(240).build(OTHERS),
			label.field("on"),
			depositedOnDisplay.readOnly().build(TIMESTAMP));
	}

	@Override
	protected List<Node> creationNodes() {
		List<Node> l = new ArrayList<>(super.creationNodes());
		l.addAll(decisionNeededApp.addAuditNodes());
		return l;
	}

	@Override
	protected List<Node> mainVerticalPaneNodes() {
		return asList(addGridPane(), depositPane(), trackedPane());
	}

	protected AppGridPane addGridPane() {
		gridPane.getChildren().clear();
		dateGridNodes("Date", orderDateDisplay, orderDatePicker, 0, 0, 1);
		currencyInputGridNodes("Amount", amountInput, 110, 2, 0);
		labelGridNode("Received from", 4, 0);
		gridPane.add(receivedFromCombo.width(180).items(service.getReceivedFromList()), 5, 0);

		labelGridNode("Type", 0, 1);
		gridPane.add(paymentCombo.width(140).items(PaymentType.values()), 1, 1);
		labelGridNode("Check No.", 2, 1);
		gridPane.add(checkIdInput.width(120).build(ID), 3, 1);
		labelGridNode("Drawn from", 4, 1);
		gridPane.add(draweeBankCombo.width(180).items(service.listBanks()), 5, 1);
		return gridPane;
	}

	private HBox depositPane() {
		return pane.centeredHorizontal(depositNodes());
	}

	protected List<Node> depositNodes() {
		return logNodes("Deposited per", depositEncodedByDisplay, depositEncodedOnDisplay);
	}

	@Override
	public void refresh() {
		super.refresh();
		orderDatePicker.setValue(service.getPaymentDate());
		refreshPayment();
		refreshInputsAfterPayment();
		depositedToDisplay.setValue(service.getDepositedTo());
		depositedOnDisplay.setValue(service.getDepositedOn());
		depositEncodedByDisplay.setValue(service.getDepositEncodedBy());
		depositEncodedOnDisplay.setValue(service.getDepositEncodedOn());
		remarksDisplay.setValue(service.getRemarks());
		decisionNeededApp.refresh(service);
	}

	private void refreshPayment() {
		amountInput.setValue(service.getValue());
	}

	private void refreshInputsAfterPayment() {
		receivedFromCombo.items(service.getReceivedFromList());
		paymentCombo.items(service.getPaymentTypes());
		refreshInputsAfterPaymentCombo();
	}

	protected void refreshInputsAfterPaymentCombo() {
		checkIdInput.setValue(service.getCheckId());
		draweeBankCombo.items(service.getDraweeBanks());
	}

	@Override
	protected void setBindings() {
		BooleanProperty canDepositCash = new SimpleBooleanProperty(service.canDepositCash());
		BooleanProperty canDepositCheck = new SimpleBooleanProperty(service.canDepositCheck());
		canPostPaymentData = new SimpleBooleanProperty(service.canPostPaymentData());
		saveButton.disableIf(saveButtonDisableBindings());
		depositButton.disableIf(isNew()
			.or(audited())
			.or(deposited())
			.or(paymentCombo.is(CASH).and(canDepositCash.not()))
			.or(paymentCombo.is(CHECK).and(canDepositCheck.not())));
		decisionButton.disableIf(isNew());
		amountInput.disableIf(noDate()
			.or(paymentCombo.isEmpty().not()));
		receivedFromComboBindings();
		paymentCombo.disableIf(receivedFromCombo.disabledProperty());
		checkIdInput.disableIf(cash());
		draweeBankCombo.disableIf(cash()
			.or(checkIdInput.isEmpty()));
		remarksDisplay.editableIf(isPosted().not());
	}

	protected BooleanBinding saveButtonDisableBindings() {
		return isPosted()
			.or(remarksDisplay.doesNotContain(CANCELLED))
			.or(canPostPaymentData.not());
	}

	private BooleanBinding audited() {
		return decisionNeededApp.isAudited();
	}

	private BooleanBinding deposited() {
		return depositedOnDisplay.isNotEmpty();
	}

	private BooleanBinding noDate() {
		return orderDatePicker.isEmpty();
	}

	protected void receivedFromComboBindings() {
		receivedFromCombo.disableIf(amountInput.isEmpty());
	}

	protected BooleanBinding cash() {
		return paymentCombo.is(CASH);
	}

	@Override
	public void goToDefaultFocus() {
		if (service.isNew())
			orderDatePicker.requestFocus();
	}

	@Override
	protected void setListeners() {
		super.setListeners();
		checkSearchButton.onAction(e -> showCheckSearchDialog());
		decisionNeededApp.setDecisionButtonOnAction(e -> showAuditDialogToValidateOrder());
		amountInput.onAction(e -> setPaymentThenCleartSucceedingNodes());
		receivedFromCombo.onAction(e -> service.setCollector(receivedFromCombo.getValue()));
		checkIdInput.onAction(e -> service.setCheckId(checkIdInput.getValue()));
		draweeBankCombo.onAction(e -> updateUponCheckAndBankValidation());
		depositButton.onAction(e -> inputDepositData());
		orderDatePicker.onAction(a -> updateUponDateValidation(orderDatePicker.getValue()));
		paymentCombo.onAction(e -> clearNodesAfterPaymentCombo());
	}

	private void showCheckSearchDialog() {
		CheckSearchDialog dialog = checkSearchDialog();
		dialog.addParent(this).start();
		String bank = dialog.getBank();
		Long checkId = dialog.getCheckId();
		if (bank != null) {
			if (checkId == 0)
				checkId = showCheckList(bank);
			open(bank, checkId);
		}
	}

	@Lookup
	CheckSearchDialog checkSearchDialog() {
		return null;
	}

	private Long showCheckList(String bank) {
		CheckListApp app = checkListApp();
		if (app == null)
			return null;
		app.bank(bank).addParent(this).start();
		return app.getSelectedKey();
	}

	@Lookup
	private CheckListApp checkListApp() {
		return null;
	}

	private void open(String bank, Long checkId) {
		try {
			service.open(bank, checkId);
			refresh();
		} catch (Exception e) {
			showErrorDialog(e);
		}
	}

	protected void showAuditDialogToValidateOrder() {
		decisionNeededApp.showDecisionDialogForValidation(this, service);
		refresh();
	}

	private void setPaymentThenCleartSucceedingNodes() {
		setPayment();
		clearPaymentInputSucceedingNodes();
	}

	private void setPayment() {
		service.setPayment(amountInput.getValue());
	}

	private void clearPaymentInputSucceedingNodes() {
		service.resetInputDataRelatedToPayment();
		refreshInputsAfterPayment();
	}

	private void updateUponCheckAndBankValidation() {
		if (service.isNew() && !checkIdInput.isEmpty().get() && !draweeBankCombo.isEmpty().get()) {
			try {
				service.validateBankCheckBeforeSetting(draweeBankCombo.getValue());
			} catch (Exception e) {
				showErrorDialog(e);
				renew();
			}
		}
	}

	private void inputDepositData() {
		DepositDialog dialog = depositDialog();
		dialog.addParent(this).start();
		if (dialog.getTimestamp() != null)
			setDepositData(dialog);
	}

	@Lookup
	DepositDialog depositDialog() {
		return null;
	}

	private void setDepositData(DepositDialog depositDialog) {
		service.setDepositData(depositDialog.getBank(), depositDialog.getTimestamp());
		save();
	}

	private void updateUponDateValidation(LocalDate d) {
		if (service.isNew() && d != null)
			try {
				service.validateOrderDateBeforeSetting(d);
			} catch (Exception e) {
				showErrorDialog(e);
			} finally {
				refresh();
				orderDatePicker.requestFocus();
			}
	}

	private void clearNodesAfterPaymentCombo() {
		service.resetInputDataRelatedToPayment();
		refreshInputsAfterPaymentCombo();
	}
}
