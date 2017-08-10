package ph.txtdis.dyvek.app;

import javafx.scene.Node;
import javafx.scene.image.WritableImage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Lookup;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import ph.txtdis.dyvek.fx.dialog.CheckPaymentDialog;
import ph.txtdis.dyvek.fx.dialog.VendorBillDialog;
import ph.txtdis.dyvek.fx.dialog.VoucherPrintingDialog;
import ph.txtdis.dyvek.service.VendorBillingService;
import ph.txtdis.fx.control.AppButton;
import ph.txtdis.fx.control.AppFieldImpl;
import ph.txtdis.info.Information;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

@Scope("prototype")
@Component("vendorBillingApp")
public class VendorBillingAppImpl
	extends AbstractBillingApp<UnassignedVendorDeliveryListApp, PurchaseAssignedDeliveryListApp, VendorBillingService>
	implements VendorBillingApp {

	private AppButton paymentButton, printButton;

	@Autowired
	private AppFieldImpl<LocalDate> billDateDisplay, paymentDateDisplay, cashAdvanceDateDisplay;

	@Autowired
	private AppFieldImpl<BigDecimal> paymentValueDisplay, cashAdvanceValueDisplay, grossWeightDisplay,
		tareWeightDisplay, ffaPercentDisplay, iodineValueDisplay;

	@Autowired
	private AppFieldImpl<Long> paymentIdDisplay, cashAdvanceIdDisplay;

	@Autowired
	private AppFieldImpl<String> billNoDisplay, bankDisplay, cashAdvanceDisplay, paidByDisplay, truckPlateNoDisplay,
		truckScaleNoDisplay, colorDisplay;

	@Autowired
	private AppFieldImpl<ZonedDateTime> paidOnDisplay;

	@Override
	protected List<AppButton> addButtons() {
		List<AppButton> b = super.addButtons();
		b.add(billActionButton = button.icon("intray").tooltip("SOA...").build());
		b.add(paymentButton = button.icon("payment").tooltip("Payment...").build());
		b.add(printButton = button.icon("print").tooltip("Voucher...").build());
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
		printButton.onAction(e -> openPrintingDialog());
	}

	private void openPaymentDialog() {
		try {
			CheckPaymentDialog d = checkPaymentDialog();
			d.addParent(this).start();
			service.setPaymentData(d.getAddedItems());
		} catch (Information i) {
			showInfoDialog(i);
		} catch (Exception e) {
			e.printStackTrace();
			showErrorDialog(e);
		} finally {
			refresh();
		}
	}

	@Lookup
	CheckPaymentDialog checkPaymentDialog() {
		return null;
	}

	private void openPrintingDialog() {
		try {
			VoucherPrintingDialog d = voucherPrintingDialog();
			WritableImage image = getScene().snapshot(null);
			d.toPrint(image).addParent(this).start();
		} catch (Exception e) {
			e.printStackTrace();
			showErrorDialog(e);
		}
	}

	@Lookup
	VoucherPrintingDialog voucherPrintingDialog() {
		return null;
	}

	@Override
	public void refresh() {
		truckPlateNoDisplay.setValue(service.getTruckPlateNo());
		truckScaleNoDisplay.setValue(service.getTruckScaleNo());
		grossWeightDisplay.setValue(service.getGrossWeight());
		tareWeightDisplay.setValue(service.getTareWeight());
		ffaPercentDisplay.setValue(service.getPercentFreeFattyAcid());
		iodineValueDisplay.setValue(service.getIodineValue());
		colorDisplay.setValue(service.getColor());
		billNoDisplay.setValue(service.getBillNo());
		billDateDisplay.setValue(service.getBillDate());
		bankDisplay.setValue(service.getBank());
		paymentIdDisplay.setValue(service.getCheckId());
		paymentDateDisplay.setValue(service.getCheckDate());
		paymentValueDisplay.setValue(service.getCheckValue());
		cashAdvanceDisplay.setValue(service.getCashAdvance());
		cashAdvanceDateDisplay.setValue(service.getCashAdvanceDate());
		cashAdvanceIdDisplay.setValue(service.getCashAdvanceId());
		cashAdvanceValueDisplay.setValue(service.getCashAdvanceValue());
		super.refresh();
	}

	@Override
	protected List<Node> mainVerticalPaneNodes() {
		List<Node> l = new ArrayList<>(super.mainVerticalPaneNodes());
		l.add(pane.centeredHorizontal(logNodes("Paid By", paidByDisplay, paidOnDisplay)));
		return l;
	}

	@Override
	protected void openBillingDialog() {
		try {
			VendorBillDialog d = vendorBillDialog();
			d.addParent(this).start();
			service.setBillData(d.getAddedItems());
		} catch (Information i) {
			showInfoDialog(i);
		} catch (Exception e) {
			e.printStackTrace();
			showErrorDialog(e);
		} finally {
			refresh();
		}
	}

	@Lookup
	VendorBillDialog vendorBillDialog() {
		return null;
	}

	@Override
	@Lookup("unassignedVendorDeliveryListApp")
	protected UnassignedVendorDeliveryListApp openOrderListApp() {
		return null;
	}

	@Override
	@Lookup("purchaseAssignedDeliveryListApp")
	protected PurchaseAssignedDeliveryListApp orderListApp() {
		return null;
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
		paymentButton.disableIf(billActedOnDisplay.isEmpty()
			.or(paidOnDisplay.isNotEmpty()));
		printButton.disableIf(paymentValueDisplay.isEmpty());
	}

	@Override
	protected void thirdGridLine() {
		truckAndScaleGridNodes();
		qualityGridNodes();
		billingGridNodes();
		checkPaymentGridNodes();
		cashAdvanceLiquidationGridNodes();
		remarksGridNodes(7, 8);
	}

	private void truckAndScaleGridNodes() {
		labelGridNode("Truck", 0, 2);
		textDisplayGridNodes("Plate No.", truckPlateNoDisplay, 110, 1, 2, 1);
		textDisplayGridNodes("Scale", truckScaleNoDisplay, 110, 3, 2, 1);
		qtyDisplayGridNodes("Gross Wt.", grossWeightDisplay, 5, 2, 1);
		qtyDisplayGridNodes("Tare Wt.", tareWeightDisplay, 7, 2, 1);
	}

	private void qualityGridNodes() {
		labelGridNode("Quality", 0, 3);
		percentDisplayGridNodes("%FFA", ffaPercentDisplay, 1, 3);
		qtyDisplayGridNodes("Iodine", iodineValueDisplay, 3, 3);
		textDisplayGridNodes("Color", colorDisplay, 110, 5, 3, 1);
	}

	@Override
	protected void billingGridNodes() {
		labelGridNode("Adjustment", 0, 4);
		qtyDisplayGridNodes("Quantity", adjustmentQtyDisplay, 1, 4);
		currencyDisplayGridNodes("Price", adjustmentPriceDisplay, 110, 3, 4);
		textDisplayGridNodes("SOA No.", billNoDisplay, 110, 5, 4, 1);
		dateDisplayGridNodes("SOA Date", billDateDisplay, 7, 4);
	}

	private void checkPaymentGridNodes() {
		textDisplayGridNodes("Paid thru", bankDisplay, 140, 0, 5, 2);
		dateDisplayGridNodes("Date", paymentDateDisplay, 3, 5);
		idDisplayGridNodes("Check No.", paymentIdDisplay, 5, 5);
		currencyDisplayGridNodes("Amount", paymentValueDisplay, 110, 7, 5);
	}

	private void cashAdvanceLiquidationGridNodes() {
		textDisplayGridNodes("& via", cashAdvanceDisplay, 140, 0, 6, 2);
		dateDisplayGridNodes("Date", cashAdvanceDateDisplay, 3, 6);
		idDisplayGridNodes("C/A No.", cashAdvanceIdDisplay, 5, 6);
		currencyDisplayGridNodes("Amount", cashAdvanceValueDisplay, 110, 7, 6);
	}
}
