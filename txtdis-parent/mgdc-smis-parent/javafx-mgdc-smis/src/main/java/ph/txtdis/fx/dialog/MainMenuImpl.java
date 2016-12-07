package ph.txtdis.fx.dialog;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import javafx.scene.layout.GridPane;
import ph.txtdis.app.AgingReceivableApp;
import ph.txtdis.app.BillableApp;
import ph.txtdis.app.CreditNoteApp;
import ph.txtdis.app.InventoryApp;
import ph.txtdis.app.PickListApp;
import ph.txtdis.app.RemittanceApp;
import ph.txtdis.app.SalesRevenueApp;
import ph.txtdis.app.SalesVolumeApp;
import ph.txtdis.app.StockTakeApp;
import ph.txtdis.app.StockTakeVarianceApp;
import ph.txtdis.app.VatApp;
import ph.txtdis.fx.control.AppButton;

@Lazy
@Component("mainMenuImpl")
public class MainMenuImpl extends AbstractSyncedMainMenu {

	@Autowired
	private AppButton agingReceivableButton, creditNoteButton, purchaseOrderButton, purchaseReceiptButton,
			salesOrderButton, salesReturnButton, returnOrderButton, badOrderButton, deliveryReportButton, invoiceButton,
			inventoryButton, salesRevenueButton, salesVolumeButton, settingsButton, pickListButton, remittanceButton,
			stockTakeButton, stockTakeReconciliationButton, vatButton;

	@Autowired
	private AgingReceivableApp agingApp;

	@Autowired
	private BillableApp purchaseOrderApp, purchaseReceiptApp, salesOrderApp, salesReturnApp, returnOrderApp, badOrderApp,
			deliveryReportApp, invoiceApp;

	@Autowired
	private CreditNoteApp creditNoteApp;

	@Autowired
	private InventoryApp inventoryApp;

	@Autowired
	private SalesRevenueApp salesRevenueApp;

	@Autowired
	private SalesVolumeApp salesVolumeApp;

	@Autowired
	private SettingsMenuImpl settingsMenu;

	@Autowired
	private PickListApp pickListApp;

	@Autowired
	private RemittanceApp remittanceApp;

	@Autowired
	private StockTakeApp stockTakeApp;

	@Autowired
	private StockTakeVarianceApp stockTakeVarianceApp;

	@Autowired
	private VatApp vatApp;

	@Override
	protected GridPane addGridPaneNodes(GridPane gp) {
		/*
		gp.add(button(purchaseOrderApp.type(PURCHASE_ORDER), purchaseOrderButton, "purchaseOrder"), 0, 0);
		gp.add(button(purchaseReceiptApp.type(PURCHASE_RECEIPT), purchaseReceiptButton, "purchaseReceipt"), 1, 0);
		gp.add(button(returnOrderApp.type(RETURN_ORDER), returnOrderButton, "returnOrder"), 2, 0);
		gp.add(button(badOrderApp.type(BAD_ORDER), badOrderButton, "badOrder"), 3, 0);
		gp.add(button(salesOrderApp.type(SALES_ORDER), salesOrderButton, "salesOrder"), 4, 0);
		gp.add(button(pickListApp, pickListButton, "pickList"), 5, 0);
		gp.add(button(salesReturnApp.type(SALES_RETURN), salesReturnButton, "salesReturn"), 6, 0);
		
		gp.add(label.menu("Purchases"), 0, 1);
		gp.add(label.menu("P/O Receipts"), 1, 1);
		gp.add(label.menu("Item Returns"), 2, 1);
		gp.add(label.menu("Bad Orders"), 3, 1);
		gp.add(label.menu("Bookings"), 4, 1);
		gp.add(label.menu("Picklists"), 5, 1);
		gp.add(label.menu("S/O Returns"), 6, 1);
		
		gp.add(button(deliveryReportApp.type(DELIVERY_REPORT), deliveryReportButton, "deliveryReport"), 0, 2);
		gp.add(button(invoiceApp.type(INVOICE), invoiceButton, "invoice"), 1, 2);
		gp.add(button(remittanceApp, remittanceButton, "remittance"), 2, 2);
		gp.add(button(agingApp, agingReceivableButton, "agingReceivable"), 3, 2);
		gp.add(button(vatApp, vatButton, "vat"), 4, 2);
		gp.add(button(salesVolumeApp, salesVolumeButton, "salesVolume"), 5, 2);
		gp.add(button(salesRevenueApp, salesRevenueButton, "salesRevenue"), 6, 2);
		
		gp.add(label.menu("D/R"), 0, 3);
		gp.add(label.menu("Invoicing"), 1, 3);
		gp.add(label.menu("Collection"), 2, 3);
		gp.add(label.menu("Aging A/R"), 3, 3);
		gp.add(label.menu("VAT"), 4, 3);
		gp.add(label.menu("Volume"), 5, 3);
		gp.add(label.menu("Revenue"), 6, 3);
		
		gp.add(button(inventoryApp, inventoryButton, "inventory"), 0, 4);
		gp.add(button(stockTakeApp, stockTakeButton, "stockTake"), 1, 4);
		gp.add(button(stockTakeVarianceApp, stockTakeReconciliationButton, "stockTakeReconciliation"), 2, 4);
		gp.add(button(upApp.type(UPLOAD), upButton, "upload"), 3, 4);
		gp.add(button(downApp.type(DOWNLOAD), downButton, "download"), 4, 4);
		gp.add(button(creditNoteApp, creditNoteButton, "creditNote"), 5, 4);
		gp.add(button(settingsMenu, settingsButton, "settings"), 6, 4);
		
		gp.add(label.menu("Inventories"), 0, 5);
		gp.add(label.menu("Stock Take"), 1, 5);
		gp.add(label.menu("Stock Recon"), 2, 5);
		gp.add(label.menu(upload()), 3, 5);
		gp.add(label.menu(download()), 4, 5);
		gp.add(label.menu("Credit Note"), 5, 5);
		gp.add(label.menu("Settings"), 6, 5);
		*/
		return gp;
	}
}
