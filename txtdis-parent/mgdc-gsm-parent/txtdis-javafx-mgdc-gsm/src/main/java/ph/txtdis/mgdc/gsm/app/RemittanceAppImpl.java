package ph.txtdis.mgdc.gsm.app;

import javafx.beans.binding.BooleanBinding;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.scene.Node;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import ph.txtdis.app.AbstractPaymentDetailedRemittanceApp;
import ph.txtdis.fx.control.AppButton;
import ph.txtdis.fx.control.AppFieldImpl;
import ph.txtdis.fx.pane.AppGridPane;
import ph.txtdis.mgdc.service.AdjustableInputtedPaymentDetailedRemittanceService;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

@Scope("prototype")
@Component("remittanceApp")
public class RemittanceAppImpl //
	extends AbstractPaymentDetailedRemittanceApp<AdjustableInputtedPaymentDetailedRemittanceService> {

	@Autowired
	private AppButton paymentReceiptButton, unvalidatedListButton;

	@Autowired
	private AppFieldImpl<String> paymentReceivedByDisplay;

	@Autowired
	private AppFieldImpl<ZonedDateTime> paymentReceivedOnDisplay;

	@Autowired
	private UnvalidatedRemittanceListApp unvalidatedListApp;

	private BooleanProperty canReceiveTransferredPayments;

	@Override
	protected List<AppButton> addButtons() {
		List<AppButton> b = new ArrayList<>(super.addButtons());
		b.add(6, paymentReceiptButton.icon("payment").tooltip("Receive payment").build());
		b.add(unvalidatedListButton.icon("list").tooltip("List unvalidated").build());
		return b;
	}

	@Override
	protected AppGridPane addGridPane() {
		super.addGridPane();
		depositAndRemarksNodesStartingAtLineNo(2);
		return gridPane;
	}

	@Override
	protected List<Node> depositNodes() {
		List<Node> l =
			new ArrayList<>(logNodes("Payment Received by", paymentReceivedByDisplay, paymentReceivedOnDisplay));
		l.addAll(super.depositNodes());
		return l;
	}

	@Override
	public void refresh() {
		super.refresh();
		canReceiveTransferredPayments.set(service.canReceiveTransferredPayments());
		paymentReceivedByDisplay.setValue(service.getPaymentReceivedBy());
		paymentReceivedOnDisplay.setValue(service.getPaymentReceivedOn());
	}

	@Override
	protected BooleanBinding saveButtonDisableBindings() {
		return super.saveButtonDisableBindings() //
			.or(draweeBankCombo.are(service.listAdjustingAccounts()) //
				.and(remarksDisplay.isEmpty()));
	}

	@Override
	protected void setBindings() {
		canReceiveTransferredPayments = new SimpleBooleanProperty(service.canReceiveTransferredPayments());
		paymentReceiptButton.disableIf(canReceiveTransferredPayments.not());
		super.setBindings();
	}

	@Override
	protected void setListeners() {
		super.setListeners();
		paymentReceiptButton.onAction(e -> setPaymentReceiptData());
		unvalidatedListButton.onAction(e -> openUnvalidatedListApp());
	}

	private void setPaymentReceiptData() {
		service.setPaymentReceiptData();
		save();
	}

	private void openUnvalidatedListApp() {
		unvalidatedListApp.addParent(this).start();
		Long id = unvalidatedListApp.getSelectedKey();
		if (id != null)
			actOn(id.toString());
	}

	@Override
	protected void showAuditDialogToValidateOrder() {
		String id = service.findWithPendingActions();
		if (id != null && !id.equals(service.getId().toString()))
			actOnPending(id);
		else
			super.showAuditDialogToValidateOrder();
	}

	private void actOnPending(String id) {
		String msg = "Validate, deposit or receive\nRemittance No. " + id + "\nfirst, to proceed.";
		messageDialog().showError(msg).addParent(this).start();
		actOn(id, "");
	}
}
