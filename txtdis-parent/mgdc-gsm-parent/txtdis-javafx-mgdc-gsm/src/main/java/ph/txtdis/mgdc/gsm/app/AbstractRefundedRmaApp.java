package ph.txtdis.mgdc.gsm.app;

import javafx.beans.binding.BooleanBinding;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.scene.Node;
import javafx.scene.layout.HBox;
import org.springframework.beans.factory.annotation.Autowired;
import ph.txtdis.app.App;
import ph.txtdis.fx.control.AppButton;
import ph.txtdis.fx.control.AppFieldImpl;
import ph.txtdis.fx.dialog.InputtedDialog;
import ph.txtdis.info.Information;
import ph.txtdis.mgdc.fx.table.BillableTable;
import ph.txtdis.mgdc.gsm.service.RefundedRmaService;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static ph.txtdis.type.Type.*;

public abstract class AbstractRefundedRmaApp< //
	RS extends RefundedRmaService, //
	BT extends BillableTable, //
	RPD extends InputtedDialog<LocalDate>> //
	extends AbstractRmaApp<RS, BT> {

	@Autowired
	private AppButton paymentButton, printButton;

	@Autowired
	private AppFieldImpl<Long> checkIdDisplay;

	@Autowired
	private AppFieldImpl<String> bankDisplay;

	@Autowired
	private RPD paymentDialog;

	private BooleanProperty isPrinted;

	@Override
	protected List<AppButton> addButtons() {
		List<AppButton> b = new ArrayList<>(super.addButtons());
		b.add(b.size() - 1, printButton);
		b.add(paymentButton);
		return b;
	}

	@Override
	protected void buildButttons() {
		super.buildButttons();
		printButton.icon("print").tooltip("Print...").build();
		paymentButton.icon("cheque").tooltip("Pay...").build();
	}

	@Override
	protected List<Node> mainVerticalPaneNodes() {
		List<Node> l = new ArrayList<>(super.mainVerticalPaneNodes());
		l.add(2, vatAndTotalPane());
		return l;
	}

	@Override
	protected HBox receivingPane() {
		List<Node> l = new ArrayList<>(receivingNodes());
		l.addAll(billingNodes("Paid"));
		return pane.centeredHorizontal(l);
	}

	@Override
	public void refresh() {
		super.refresh();
		refreshCheckDetails();
		orderNoDisplay.setValue(service.getInvoiceNo());
		isPrinted.set(service.getPrintedOn() != null);
	}

	private void refreshCheckDetails() {
		String[] checkDetails = service.getCheckDetails();
		bankDisplay.setValue(bank(checkDetails));
		checkIdDisplay.setValue(checkId(checkDetails));
	}

	private String bank(String[] checkDetails) {
		return checkDetails == null ? null : checkDetails[0];
	}

	private Long checkId(String[] checkDetails) {
		return checkDetails == null ? null : Long.valueOf(checkDetails[1]);
	}

	@Override
	protected void refreshSummaryPane() {
		super.refreshSummaryPane();
		vatableDisplay.setValue(service.getVatable());
		vatDisplay.setValue(service.getVat());
	}

	@Override
	protected void setBindings() {
		super.setBindings();
		printButton.disableIf(notValidReturn() //
			.or(isPrinted));
		paymentButton.disableIf(notValidReturn() //
			.or(receivedOnDisplay.isEmpty())//
			.or(isPaid()));
	}

	private BooleanBinding isPaid() {
		return billedOnDisplay.isNotEmpty();
	}

	@Override
	protected void setBooleanProperties() {
		super.setBooleanProperties();
		isPrinted = new SimpleBooleanProperty(false);
	}

	@Override
	protected void setListeners() {
		super.setListeners();
		printButton.onAction(e -> print());
		paymentButton.onAction(e -> saveInputtedReturnPaymentData());
	}

	private void print() {
		try {
			service.print();
			refresh();
		} catch (Exception e) {
			showErrorDialog(e);
		}
	}

	private void saveInputtedReturnPaymentData() {
		((App) paymentDialog).addParent(this).start();
		List<LocalDate> d = paymentDialog.getAddedItems();
		saveInputtedReturnPaymentData(d);
	}

	private void saveInputtedReturnPaymentData(List<LocalDate> d) {
		if (d != null) {
			saveInputtedReturnPaymentData(d.get(0));
		}
		else
			service.clearInputtedReturnPaymentData();
	}

	private void saveInputtedReturnPaymentData(LocalDate d) {
		try {
			service.saveReturnPaymentData(d);
		} catch (Information i) {
			messageDialog.show(i).addParent(this).start();
		} catch (Exception e) {
			showErrorDialog(e);
		} finally {
			refresh();
		}
	}

	@Override
	protected void secondGridLine() {
		gridPane.add(label.field("S/I No."), 0, 1);
		gridPane.add(orderNoDisplay.readOnly().width(WIDTH).build(CODE), 1, 1);
		gridPane.add(label.field("Bank"), 2, 1);
		gridPane.add(bankDisplay.readOnly().width(WIDTH).build(TEXT), 3, 1);
		gridPane.add(label.field("Check No."), 4, 1);
		gridPane.add(checkIdDisplay.readOnly().width(WIDTH).build(ID), 5, 1);
		customerWithoutDueDateGridLine(2, 5);
	}

	@Override
	protected void refreshLogNodes() {
		super.refreshLogNodes();
		billedByDisplay.setValue(service.getBilledBy());
		billedOnDisplay.setValue(service.getBilledOn());
	}

	@Override
	protected void setButtonBindings() {
		invalidateButton.disableIf(isNew() //
			.or(notValidReturn()) //
			.or(isPaid()));
		receiptButton.disableIf(isPrinted.not() //
			.or(notValidReturn()) //
			.or(receivedOnDisplay.isNotEmpty()) //
			.or(isPaid()));
		saveButton.disableIf(isPosted());
	}
}
