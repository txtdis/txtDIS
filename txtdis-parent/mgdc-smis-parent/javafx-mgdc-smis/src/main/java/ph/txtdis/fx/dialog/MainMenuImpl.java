package ph.txtdis.fx.dialog;

import static ph.txtdis.type.ModuleType.BAD_ORDER;
import static ph.txtdis.type.ModuleType.DELIVERY_REPORT;
import static ph.txtdis.type.ModuleType.DOWNLOAD;
import static ph.txtdis.type.ModuleType.INVOICE;
import static ph.txtdis.type.ModuleType.PURCHASE_ORDER;
import static ph.txtdis.type.ModuleType.PURCHASE_RECEIPT;
import static ph.txtdis.type.ModuleType.RETURN_ORDER;
import static ph.txtdis.type.ModuleType.SALES_ORDER;
import static ph.txtdis.type.ModuleType.SALES_RETURN;
import static ph.txtdis.type.ModuleType.UPLOAD;
import static ph.txtdis.util.SpringUtil.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import ph.txtdis.app.AgingReceivableApp;
import ph.txtdis.app.CreditNoteApp;
import ph.txtdis.app.InventoryApp;
import ph.txtdis.app.PickListApp;
import ph.txtdis.app.RemittanceApp;
import ph.txtdis.app.SalesApp;
import ph.txtdis.app.SalesRevenueApp;
import ph.txtdis.app.SalesVolumeApp;
import ph.txtdis.app.Startable;
import ph.txtdis.app.StockTakeApp;
import ph.txtdis.app.SyncApp;
import ph.txtdis.app.VatApp;
import ph.txtdis.fx.control.AppButton;
import ph.txtdis.fx.control.LabelFactory;
import ph.txtdis.service.ScriptService;
import ph.txtdis.service.ServerService;
import ph.txtdis.util.FontIcon;
import ph.txtdis.util.StyleSheet;

@Lazy
@Component("mainMenuImpl")
public class MainMenuImpl extends Stage implements MainMenu {

	@Autowired
	private AppButton agingReceivableButton, creditNoteButton, purchaseOrderButton, purchaseReceiptButton,
			salesOrderButton, salesReturnButton, returnOrderButton, badOrderButton, deliveryReportButton, invoiceButton,
			inventoryButton, salesRevenueButton, salesVolumeButton, settingsButton, pickListButton, remittanceButton,
			stockTakeButton, stockTakeReconciliationButton, upButton, downButton, vatButton;

	@Autowired
	private AgingReceivableApp agingApp;

	@Autowired
	private CreditNoteApp creditNoteApp;

	@Autowired
	private SalesApp purchaseOrderApp, purchaseReceiptApp, salesOrderApp, salesReturnApp, returnOrderApp, badOrderApp,
			deliveryReportApp, invoiceApp;

	@Autowired
	private InventoryApp inventoryApp;

	@Autowired
	private SalesRevenueApp salesRevenueApp;

	@Autowired
	private SalesVolumeApp salesVolumeApp;

	@Autowired
	private SettingsMenu settingsMenu;

	@Autowired
	private PickListApp pickListApp;

	@Autowired
	private RemittanceApp remittanceApp;

	@Autowired
	private StockTakeApp stockTakeApp;

	@Autowired
	private SalesApp stockTakeReconciliationApp;

	@Autowired
	private SyncApp upApp, downApp;

	@Autowired
	private VatApp vatApp;

	@Autowired
	private LabelFactory label;

	@Autowired
	private MessageDialog dialog;

	@Autowired
	private ServerService restServerService;

	@Autowired
	private ScriptService scriptService;

	@Autowired
	private StyleSheet styleSheet;

	@Override
	public void display() {
		getIcons().add(new FontIcon("\ue826"));
		setTitle("txtDIS Menu");
		setScene(createScene());
		styleSheet.update(user().getStyle());
		setOnCloseRequest(e -> checkUnpostedTransactions(e));
		show();
	}

	private Scene createScene() {
		Scene s = new Scene(dialogBox());
		s.getStylesheets().add("/css/base.css");
		return s;
	}

	private Parent dialogBox() {
		HBox b = new HBox(gridPane());
		b.setPadding(new Insets(10));
		b.setAlignment(Pos.CENTER);
		return b;
	}

	private void checkUnpostedTransactions(WindowEvent e) {
		if (restServerService.isOffSite() && scriptService.unpostedTransactionsExist())
			showPostOrExitDialog(e);
	}

	private void showPostOrExitDialog(WindowEvent we) {
		dialog.showOption("Unposted transactions exist;\nproceed, how?", "Post", "Exit");
		dialog.setOnOptionSelection(e -> postUnpostedTransaction(we));
		dialog.setOnDefaultSelection(e -> Platform.exit());
		dialog.addParent(this).start();
	}

	private void postUnpostedTransaction(WindowEvent e) {
		e.consume();
		dialog.close();
		upApp.start();
	}

	private GridPane gridPane() {
		GridPane gp = new GridPane();
		gp.setHgap(5);
		gp.setVgap(5);
		gp.setAlignment(Pos.CENTER);

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
		gp.add(button(stockTakeReconciliationApp, stockTakeReconciliationButton, "stockTakeReconciliation"), 2, 4);
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
		return gp;
	}

	private AppButton button(Startable app, AppButton button, String icon) {
		button.fontSize(44).icon(icon).build();
		button.setOnAction(e -> app.start());
		return button;
	}

	private String upload() {
		return restServerService.isOffSite() ? "Post" : "Upload";
	}

	private String download() {
		return restServerService.isOffSite() ? "Replicate" : "Download";
	}
}
