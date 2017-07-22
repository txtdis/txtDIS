package ph.txtdis.dyvek.app;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javafx.scene.Node;
import ph.txtdis.dyvek.fx.dialog.CheckPaymentDialogImpl;
import ph.txtdis.dyvek.fx.dialog.VendorBillDialogImpl;
import ph.txtdis.dyvek.service.VendorBillingService;
import ph.txtdis.fx.control.AppButtonImpl;
import ph.txtdis.fx.control.AppFieldImpl;
import ph.txtdis.info.Information;

@Scope("prototype")
@Component("vendorBillingApp")
public class VendorBillingAppImpl //
		extends AbstractBillingApp<UnassignedVendorDeliveryListApp, PurchaseAssignedDeliveryListApp, VendorBillingService> //
		implements VendorBillingApp {

	@Autowired
	private AppButtonImpl paymentButton;

	@Autowired
	private AppFieldImpl<LocalDate> billDateDisplay, paymentDate1Display, paymentDate2Display;

	@Autowired
	private AppFieldImpl<BigDecimal> amount1Display, amount2Display;

	@Autowired
	private AppFieldImpl<Long> paymentId1Display, paymentId2Display;

	@Autowired
	private AppFieldImpl<String> billNoDisplay, bank1Display, bank2Display, paidByDisplay;

	@Autowired
	private AppFieldImpl<ZonedDateTime> paidOnDisplay;

	@Autowired
	private CheckPaymentDialogImpl paymentDialog;

	@Autowired
	private VendorBillDialogImpl billDialog;

	@Override
	protected List<AppButtonImpl> addButtons() {
		List<AppButtonImpl> b = super.addButtons();
		b.add(billActionButton.icon("intray").tooltip("SOA...").build());
		b.add(paymentButton.icon("payment").tooltip("Payment...").build());
		return b;
	}

	@Override
	protected String billActedByPrompt() {
		return "Bill Received by";
	}

	@Override
	protected void buttonListeners() {
		super.buttonListeners();
		paymentButton.onAction(e -> openPaymentDialog());
	}

	private void openPaymentDialog() {
		try {
			paymentDialog.addParent(this).start();
			service.setPaymentData(paymentDialog.getAddedItems());
		} catch (Information i) {
			dialog.show(i).addParent(this).start();
		} catch (Exception e) {
			e.printStackTrace();
			showErrorDialog(e);
		} finally {
			refresh();
		}
	}

	@Override
	protected void gridLine3Billing() {
		super.gridLine3Billing();
		textDisplayGridNodes("Bill No.", billNoDisplay, 110, 5, 2, 1);
		dateDisplayGridNodes("Bill Date", billDateDisplay, 7, 2);
	}

	private void gridLine4CashAdvanceLiquidation() {
		textDisplayGridNodes("Paid thru", bank1Display, 140, 0, 3, 2);
		dateDisplayGridNodes("Date", paymentDate1Display, 3, 3);
		idDisplayGridNodes("No.", paymentId1Display, 5, 3);
		currencyDisplayGridNodes("Amount", amount1Display, 110, 7, 3);
	}

	private void gridLine5CheckPayment() {
		textDisplayGridNodes("& via", bank2Display, 280, 0, 4, 2);
		dateDisplayGridNodes("Date", paymentDate2Display, 3, 4);
		idDisplayGridNodes("No.", paymentId2Display, 5, 4);
		currencyDisplayGridNodes("Amount", amount2Display, 110, 7, 4);
	}

	@Override
	protected List<Node> mainVerticalPaneNodes() {
		List<Node> l = new ArrayList<>(super.mainVerticalPaneNodes());
		l.add(box.forHorizontalPane(logNodes("Paid By", paidByDisplay, paidOnDisplay)));
		return l;
	}

	@Override
	protected void openBillingDialog() {
		try {
			billDialog.addParent(this).start();
			service.setBillData(billDialog.getAddedItems());
		} catch (Information i) {
			dialog.show(i).addParent(this).start();
		} catch (Exception e) {
			e.printStackTrace();
			showErrorDialog(e);
		} finally {
			refresh();
		}
	}

	@Override
	public void refresh() {
		billNoDisplay.setValue(service.getBillNo());
		billDateDisplay.setValue(service.getBillDate());
		bank1Display.setValue(service.getBank());
		paymentId1Display.setValue(service.getCheckId());
		paymentDate1Display.setValue(service.getCheckDate());
		super.refresh();
	}

	@Override
	protected void refreshLogNodes() {
		super.refreshLogNodes();
		paidByDisplay.setValue(service.getPaymentActedBy());
		paidOnDisplay.setValue(service.getPaymentActedOn());
	}

	@Override
	protected void setBindings() {
		super.setBindings();
		paymentButton.disableIf(billActedOnDisplay.isEmpty() //
				.or(paidOnDisplay.isNotEmpty()));
	}

	@Override
	protected void thirdGridLine() {
		gridLine3Billing();
		gridLine4CashAdvanceLiquidation();
		gridLine5CheckPayment();
		remarksGridNodes(4, 8);
	}
}
