package ph.txtdis.app;

import static ph.txtdis.type.PaymentType.CASH;
import static ph.txtdis.type.PaymentType.CHECK;
import static ph.txtdis.type.PaymentType.values;
import static ph.txtdis.type.Type.CURRENCY;
import static ph.txtdis.type.Type.ID;
import static ph.txtdis.type.Type.OTHERS;
import static ph.txtdis.type.Type.TEXT;
import static ph.txtdis.type.Type.TIMESTAMP;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import javafx.beans.binding.BooleanBinding;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.scene.Node;
import javafx.scene.layout.HBox;
import ph.txtdis.dto.Customer;
import ph.txtdis.dto.Remittance;
import ph.txtdis.fx.control.AppButton;
import ph.txtdis.fx.control.AppCombo;
import ph.txtdis.fx.control.AppField;
import ph.txtdis.fx.control.LocalDatePicker;
import ph.txtdis.fx.dialog.CheckSearchDialog;
import ph.txtdis.fx.dialog.DepositDialog;
import ph.txtdis.fx.pane.AppGridPane;
import ph.txtdis.service.RemittanceService;
import ph.txtdis.type.PaymentType;

public abstract class AbstractRemittanceApp extends AbstractIdApp<RemittanceService, Remittance, Long, Long>
		implements RemittanceApp {

	private static final String PROMPT = "Select date whose first entry will opened";

	@Autowired
	private AppButton checkSearchButton, depositButton, historyButton, openByDateButton;

	@Autowired
	private AppCombo<String> collectorCombo;

	@Autowired
	private AppField<Long> checkIdInput;

	@Autowired
	private AppField<BigDecimal> amountInput;

	@Autowired
	private AppField<String> depositorBankDisplay, depositorDisplay;

	@Autowired
	private AppField<ZonedDateTime> depositedOnDisplay, depositorOnDisplay;

	@Autowired
	private LocalDatePicker paymentDatePicker;

	@Autowired
	private CheckSearchDialog checkSearchDialog;

	@Autowired
	private DepositDialog depositDialog;

	@Autowired
	private PaymentHistoryApp historyApp;

	@Autowired
	protected AppCombo<Customer> draweeBankCombo;

	@Autowired
	protected AppCombo<PaymentType> paymentCombo;

	private BooleanProperty userAllowedToMakeCashDeposits, userAllowedToMakeCheckDeposits;

	protected BooleanProperty userAllowedToReceiveFundTransfer;

	protected BooleanProperty userAllowedToPostCollectionData;

	@Override
	public void refresh() {
		super.refresh();
		paymentDatePicker.setValue(service.getPaymentDate());
		amountInput.setValue(service.getValue());
		refreshInputsAfterPaymentInput();
		depositorBankDisplay.setValue(service.getDepositorBank());
		depositedOnDisplay.setValue(service.getDepositedOn());
		depositorDisplay.setValue(service.getDepositor());
		depositorOnDisplay.setValue(service.getDepositorOn());
		remarksDisplay.setValue(service.getRemarks());
		decisionNeededApp.refresh(service);
	}

	@Override
	public void setFocus() {
		if (service.isNew())
			paymentDatePicker.requestFocus();
	}

	private AppButton auditButton() {
		return decisionButton = decisionNeededApp.addDecisionButton();
	}

	protected BooleanBinding audited() {
		return decisionNeededApp.isAudited();
	}

	private HBox basicInfoBox() {
		return box.forGridGroup(paymentDatePicker, //
				label.field("Amount"), amountInput.build(CURRENCY), //
				label.field("Received from"), collectorCombo.width(180).items(service.getCollectorNames()));
	}

	protected BooleanBinding cash() {
		return paymentCombo.is(CASH);
	}

	private HBox checkBox() {
		return box.forGridGroup(//
				paymentCombo.items(values()), //
				label.field("Check No."), checkIdInput.width(120).build(ID), //
				label.field("Bank"), draweeBankCombo.items(service.getBanks()));
	}

	private void clearNodesAfterPaymentCombo() {
		service.resetInputDataRelatedToPayment();
		refreshInputsAfterPaymentCombo();
	}

	private void clearPaymentInputSucceedingNodes() {
		service.resetInputDataRelatedToPayment();
		refreshInputsAfterPaymentInput();
	}

	private HBox depositBox() {
		return box.forGridGroup(depositorBankDisplay.readOnly().width(240).build(OTHERS), //
				label.field("on"), depositedOnDisplay.readOnly().build(TIMESTAMP), //
				label.field("per"), depositorDisplay.readOnly().width(120).build(TEXT), //
				label.field("on"), depositorOnDisplay.readOnly().build(TIMESTAMP));
	}

	protected BooleanBinding deposited() {
		return depositorOnDisplay.isNotEmpty();
	}

	protected AppGridPane addGridPane() {
		remarksDisplay.build();
		gridPane.getChildren().clear();
		gridPane.add(label.field("Date"), 0, 0);
		gridPane.add(basicInfoBox(), 1, 0);
		gridPane.add(label.field("Type"), 0, 1);
		gridPane.add(checkBox(), 1, 1);
		return gridPane;
	}

	protected void depositAndRemarksNodesStartingAtLineNo(int line) {
		gridPane.add(label.field("Deposited to"), 0, line);
		gridPane.add(depositBox(), 1, line);
		gridPane.add(label.field("Remarks"), 0, ++line);
		gridPane.add(remarksDisplay.get(), 1, line, 1, 2);
	}

	private void inputDepositData() {
		depositDialog.addParent(this).start();
		if (depositDialog.getTimestamp() != null)
			setDepositData();
	}

	private BooleanBinding noDate() {
		return paymentDatePicker.isEmpty();
	}

	private void open(Customer bank, Long checkId) {
		try {
			service.open(bank, checkId);
			refresh();
		} catch (Exception e) {
			showErrorDialog(e);
		}
	}

	private void openHistoryApp() {
		historyApp.addParent(this).start();
		Long id = historyApp.getSelectedId();
		if (id != null)
			actOn(id.toString());
	}

	protected void refreshInputsAfterPaymentCombo() {
		checkIdInput.setValue(service.getCheckId());
		draweeBankCombo.items(service.getDraweeBanks());
	}

	private void refreshInputsAfterPaymentInput() {
		collectorCombo.items(service.getCollectorNames());
		paymentCombo.items(service.getPaymentTypes());
		refreshInputsAfterPaymentCombo();
	}

	private void setDepositData() {
		service.setDepositData(depositDialog.getBank(), depositDialog.getTimestamp());
		save();
	}

	private void setPayment() {
		service.setPayment(amountInput.getValue());
		clearPaymentInputSucceedingNodes();
	}

	private void showAuditDialogToValidateOrder() {
		decisionNeededApp.showDecisionDialogForValidation(this, service);
		refresh();
	}

	private void showCheckSearchDialog() {
		checkSearchDialog.addParent(this).start();
		Customer bank = checkSearchDialog.getBank();
		Long checkId = checkSearchDialog.getCheckId();
		if (bank != null)
			open(bank, checkId);
	}

	private void showOpenByDateDialog() {
		String h = service.getOpenDialogHeader();
		openByDateDialog.header(h).prompt(PROMPT).addParent(this).start();
		LocalDate d = openByDateDialog.getDate();
		if (d != null)
			open(d);
	}

	private void updateUponCheckAndBankValidation() {
		if (service.isNew() && !checkIdInput.isEmpty().get()) {
			try {
				service.validateBankCheckBeforeSetting(draweeBankCombo.getValue());
			} catch (Exception e) {
				showErrorDialog(e);
				checkIdInput.setValue(null);
				draweeBankCombo.items(service.getBanks());
				checkIdInput.requestFocus();
			}
		}
	}

	private void updateUponDateValidation(LocalDate d) {
		if (service.isNew() && d != null)
			try {
				service.validateOrderDateBeforeSetting(d);
			} catch (Exception e) {
				showErrorDialog(e);
			} finally {
				refresh();
				paymentDatePicker.requestFocus();
			}
	}

	@Override
	protected List<AppButton> addButtons() {
		List<AppButton> b = new ArrayList<>(super.addButtons());
		b.add(historyButton.icon("list").tooltip("Show collection records\nof the last 15 days").build());
		b.add(2, openByDateButton.icon("openByDate").tooltip("Open a date's\nfirst entry").build());
		b.add(checkSearchButton.icon("checkSearch").tooltip("Find a check").build());
		b.add(depositButton.icon("deposit").tooltip("Enter deposit\ndata").build());
		b.add(auditButton());
		return b;
	}

	@Override
	protected List<Node> mainVerticalPaneNodes() {
		return Arrays.asList(addGridPane(), trackedPane());
	}

	@Override
	protected void save() {
		service.setRemarks(remarksDisplay.getValue());
		super.save();
	}

	@Override
	protected void setBindings() {
		userAllowedToMakeCashDeposits = new SimpleBooleanProperty(service.isUserAllowedToMakeCashDeposits());
		userAllowedToMakeCheckDeposits = new SimpleBooleanProperty(service.isUserAllowedToMakeCheckDeposits());
		userAllowedToPostCollectionData = new SimpleBooleanProperty(service.isUserAllowedToPostRemittanceData());
		userAllowedToReceiveFundTransfer = new SimpleBooleanProperty(service.isUserAllowedToReceiveFundTransfer());
		setSaveButtonBindings();
		depositButton.disableIf(notPosted()//
				.or(deposited())//
				.or(audited())//
				.or(paymentCombo.is(CASH).and(userAllowedToMakeCashDeposits.not()))//
				.or(paymentCombo.is(CHECK).and(userAllowedToMakeCheckDeposits.not())));
		decisionButton.disableIf(notPosted());
		amountInput.disableIf(noDate().or(paymentCombo.isEmpty().not()));
		collectorCombo.disableIf(amountInput.isEmpty());
		paymentCombo.disableIf(collectorCombo.disabledProperty());
		checkIdInput.disableIf(cash());
		draweeBankCombo.disableIf(cash()//
				.or(checkIdInput.isEmpty()));
		remarksDisplay.editableIf(isPosted().not());
	}

	protected void setSaveButtonBindings() {
		saveButton.disableIf(isPosted()//
				.or(remarksDisplay.isNot("CANCELLED"))//
				.or(userAllowedToPostCollectionData.not()));
	}

	@Override
	protected void setListeners() {
		super.setListeners();
		checkSearchButton.setOnAction(e -> showCheckSearchDialog());
		historyButton.setOnAction(e -> openHistoryApp());
		openByDateButton.setOnAction(e -> showOpenByDateDialog());
		decisionNeededApp.setDecisionButtonOnAction(e -> showAuditDialogToValidateOrder());
		amountInput.setOnAction(e -> setPayment());
		collectorCombo.setOnAction(e -> service.setCollector(collectorCombo.getValue()));
		checkIdInput.setOnAction(e -> service.setCheckId(checkIdInput.getValue()));
		draweeBankCombo.setOnAction(e -> updateUponCheckAndBankValidation());
		depositButton.setOnAction(e -> inputDepositData());
		paymentDatePicker.setOnAction(a -> updateUponDateValidation(paymentDatePicker.getValue()));
		paymentCombo.setOnAction(e -> clearNodesAfterPaymentCombo());
	}

	@Override
	protected HBox trackedPane() {
		HBox b = super.trackedPane();
		List<Node> l = new ArrayList<>(b.getChildren());
		l.addAll(decisionNeededApp.addAuditNodes());
		b.getChildren().setAll(l);
		return b;
	}
}
