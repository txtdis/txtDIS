package ph.txtdis.mgdc.gsm.fx.dialog;

import javafx.scene.layout.GridPane;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import ph.txtdis.app.*;
import ph.txtdis.fx.control.AppButton;
import ph.txtdis.fx.dialog.AbstractMainMenu;
import ph.txtdis.fx.dialog.SettingsMenu;
import ph.txtdis.mgdc.app.*;
import ph.txtdis.mgdc.gsm.app.AgingReceivableApp;
import ph.txtdis.mgdc.gsm.app.CreditNoteAppImpl;
import ph.txtdis.mgdc.gsm.app.SalesVolumeAppImpl;
import ph.txtdis.mgdc.gsm.app.VatApp;

import static ph.txtdis.type.ModuleType.*;

@Scope("prototype")
@Component("mainMenu")
public class MainMenuImpl
	extends AbstractMainMenu {

	@Autowired
	private AppButton agingReceivableButton, creditNoteButton, exTruckButton, sivButton, salesOrderButton,
		receivingReportButton, goodRmaButton,
		badRmaButton, deliveryReportButton, invoiceButton, inventoryButton, salesRevenueButton, salesVolumeButton,
		settingsButton, pickListButton,
		remittanceButton, stockTakeButton, stockTakeVarianceButton, vatButton, upButton, downButton;

	@Autowired
	private AgingReceivableApp agingApp;

	@Autowired
	private MultiTypeBillingApp deliveryReportApp, invoiceApp;

	@Autowired
	private CreditNoteAppImpl creditNoteApp;

	@Autowired
	private BadRmaApp badRmaApp;

	@Autowired
	private GoodRmaApp goodRmaApp;

	@Autowired
	private InventoryApp inventoryApp;

	@Autowired
	private DeliveryReportApp sivApp;

	@Autowired
	private PickListApp pickListApp;

	@Autowired
	private RemittanceApp remittanceApp;

	@Autowired
	private SalesOrderApp exTruckApp, salesOrderApp;

	@Autowired
	private ReceivingReportApp receivingReportApp;

	@Autowired
	private SalesRevenueApp salesRevenueApp;

	@Autowired
	private SalesVolumeAppImpl salesVolumeApp;

	@Autowired
	private SettingsMenu settingsMenu;

	@Autowired
	private StockTakeApp stockTakeApp;

	@Autowired
	private StockTakeVarianceApp stockTakeVarianceApp;

	@Autowired
	private VatApp vatApp;

	@Override
	protected GridPane addGridPaneNodes(GridPane gp) {
		gp.add(button(sivApp, sivButton, "siv"), 0, 0);
		gp.add(button(goodRmaApp, goodRmaButton, "goodRma"), 1, 0);
		gp.add(button(badRmaApp, badRmaButton, "badRma"), 2, 0);
		gp.add(button(exTruckApp.type(EX_TRUCK), exTruckButton, "loadOrder"), 3, 0);
		gp.add(button(salesOrderApp.type(SALES_ORDER), salesOrderButton, "salesOrder"), 4, 0);
		gp.add(button(pickListApp, pickListButton, "pickList"), 5, 0);
		gp.add(button(receivingReportApp, receivingReportButton, "receivingReport"), 6, 0);

		gp.add(label.menu("SIV"), 0, 1);
		gp.add(label.menu("Returns"), 1, 1);
		gp.add(label.menu("Bad Order"), 2, 1);
		gp.add(label.menu("Ex-Truck"), 3, 1);
		gp.add(label.menu("Pre-Sell"), 4, 1);
		gp.add(label.menu("Load Out"), 5, 1);
		gp.add(label.menu("Load In"), 6, 1);

		gp.add(button(deliveryReportApp.type(DELIVERY_REPORT), deliveryReportButton, "deliveryReport"), 0, 2);
		gp.add(button(invoiceApp.type(INVOICE), invoiceButton, "invoice"), 1, 2);
		gp.add(button(remittanceApp, remittanceButton, "remittance"), 2, 2);
		gp.add(button(agingApp, agingReceivableButton, "agingReceivable"), 3, 2);
		gp.add(button(vatApp, vatButton, "vat"), 4, 2);
		gp.add(button(salesVolumeApp, salesVolumeButton, "salesVolume"), 5, 2);
		gp.add(button(salesRevenueApp, salesRevenueButton, "salesRevenue"), 6, 2);

		gp.add(label.menu("D/R"), 0, 3);
		gp.add(label.menu("Invoice"), 1, 3);
		gp.add(label.menu("Remittance"), 2, 3);
		gp.add(label.menu("Aging A/R"), 3, 3);
		gp.add(label.menu("VAT"), 4, 3);
		gp.add(label.menu("STT"), 5, 3);
		gp.add(label.menu("Revenue"), 6, 3);

		gp.add(button(inventoryApp, inventoryButton, "inventory"), 0, 4);
		gp.add(button(stockTakeApp, stockTakeButton, "stockTake"), 1, 4);
		gp.add(button(stockTakeVarianceApp, stockTakeVarianceButton, "stockTakeVariance"), 2, 4);
		gp.add(button(null, upButton, "upload"), 3, 4);
		gp.add(button(null, downButton, "download"), 4, 4);
		gp.add(button(creditNoteApp, creditNoteButton, "creditNote"), 5, 4);
		gp.add(button(settingsMenu, settingsButton, "settings"), 6, 4);

		gp.add(label.menu("Inventory"), 0, 5);
		gp.add(label.menu("Stock Take"), 1, 5);
		gp.add(label.menu("Stock Recon"), 2, 5);
		gp.add(label.menu("Upload"), 3, 5);
		gp.add(label.menu("Download"), 4, 5);
		gp.add(label.menu("Credit Note"), 5, 5);
		gp.add(label.menu("Settings"), 6, 5);

		salesOrderApp.setOnShowing(e -> exTruckApp.close());
		exTruckApp.setOnShowing(e -> salesOrderApp.close());
		deliveryReportApp.setOnShowing(e -> invoiceApp.close());
		invoiceApp.setOnShowing(e -> deliveryReportApp.close());

		return gp;
	}
}
