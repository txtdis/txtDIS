package ph.txtdis.dyvek.app;

import javafx.scene.Node;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import ph.txtdis.app.AbstractPaymentDetailedRemittanceApp;
import ph.txtdis.dyvek.service.VendorAndClientCheckPaymentDetailedRemittanceService;
import ph.txtdis.fx.control.AppFieldImpl;
import ph.txtdis.fx.control.LocalDatePicker;
import ph.txtdis.fx.pane.AppGridPane;
import ph.txtdis.service.RemittanceService;

import java.time.LocalDate;

import static ph.txtdis.type.Type.*;

@Scope("prototype")
@Component("remittanceApp")
public class RemittanceAppImpl 
	extends AbstractPaymentDetailedRemittanceApp<VendorAndClientCheckPaymentDetailedRemittanceService> {

	@Autowired
	private LocalDatePicker checkDatePicker;

	@Autowired
	private AppFieldImpl<LocalDate> checkDateDisplay;

	@Override
	protected AppGridPane addGridPane() {
		gridPane.getChildren().clear();
		paymentReceiptGridNodes();
		chequeGridNodes();
		depositGridNodes();
		remarksGridNodes(3, 5);
		return gridPane;
	}

	private void paymentReceiptGridNodes() {
		comboAndInputGridNodes( 
			"Received from", 
			receivedFromCombo.width(180).items(service.getReceivedFromList()), 
			"Amount", 
			amountInput.width(110).build(CURRENCY), 
			0, 
			1);
		dateGridNodes( 
			"Date", 
			orderDateDisplay, 
			orderDatePicker, 
			4, 
			0, 
			1);
	}

	private void chequeGridNodes() {
		comboAndInputGridNodes( 
			"Drawn from", 
			draweeBankCombo.width(180).items(service.listBanks()), 
			"Check No.", 
			checkIdInput.width(110).build(ID), 
			1, 
			1);
		dateGridNodes(
			"Check Date", 
			checkDateDisplay, 
			checkDatePicker, 
			4, 
			1, 
			1);
	}

	private void depositGridNodes() {
		gridPane.add(label.field("Deposited to"), 0, 2);
		gridPane.add(depositGridNode(), 1, 2, 5, 1);
	}

	private Node depositGridNode() {
		return pane.forGridGroup(
			depositedToDisplay.readOnly().width(180).build(OTHERS), 
			label.field("on"), 
			depositedOnDisplay.readOnly().build(TIMESTAMP));
	}

	@Override
	protected void orderDateBinding() {
	}

	@Override
	protected void receivedFromComboBindings() {
		receivedFromCombo.disable();
	}

	@Override
	public void refresh() {
		super.refresh();
		checkDatePicker.setValue(service.getCheckDate());
		checkDateDisplay.setValue(service.getCheckDate());
	}

	@Override
	protected void renew() {
		super.renew();
		receivedFromCombo.enable();
		receivedFromCombo.requestFocus();
	}

	@Override
	protected void setBindings() {
		super.setBindings();
		table.editableIf(isNew());
	}
}
