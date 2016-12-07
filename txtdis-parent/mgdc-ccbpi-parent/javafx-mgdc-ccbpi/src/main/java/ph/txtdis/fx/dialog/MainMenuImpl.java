package ph.txtdis.fx.dialog;

import static ph.txtdis.type.BillableType.DOWNLOAD;
import static ph.txtdis.type.BillableType.UPLOAD;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import javafx.scene.layout.GridPane;
import ph.txtdis.app.BadRmaReplacementApp;
import ph.txtdis.app.BillableApp;
import ph.txtdis.app.BlanketBalanceApp;
import ph.txtdis.app.BookingVarianceApp;
import ph.txtdis.app.DeliveryListApp;
import ph.txtdis.app.InventoryApp;
import ph.txtdis.app.ItemApp;
import ph.txtdis.app.LoadManifestApp;
import ph.txtdis.app.LoadReturnApp;
import ph.txtdis.app.PaymentApp;
import ph.txtdis.app.PickListApp;
import ph.txtdis.app.RemittanceApp;
import ph.txtdis.app.RemittanceVarianceApp;
import ph.txtdis.app.RouteApp;
import ph.txtdis.app.StockTakeApp;
import ph.txtdis.app.StockTakeVarianceApp;
import ph.txtdis.app.TruckApp;
import ph.txtdis.fx.control.AppButton;
import ph.txtdis.fx.control.LabelFactory;

@Lazy
@Component("mainMenuImpl")
public class MainMenuImpl extends AbstractSyncedMainMenu {

	@Autowired
	private AppButton deliveryListButton, loadManifestButton, orderConfirmationButton, bookingVarianceButton,
			pickListButton, orderReturnButton, loadReturnButton, //
			badRmaButton, blanketBalanceButton, remittanceButton, remittanceVarianceButton, deliveryReturnButton,
			paymentButton, upButton, downButton, //
			inventoryButton, stockTakeButton, stockTakeVarianceButton, itemButton, routeButton, truckButton, userButton,
			styleButton;

	@Autowired
	private LabelFactory label;

	@Autowired
	private BadRmaReplacementApp badRmaApp;

	@Autowired
	private BlanketBalanceApp blanketBalanceApp;

	@Autowired
	private BookingVarianceApp bookingVarianceApp;

	@Autowired
	private DeliveryListApp deliveryListApp;

	@Autowired
	private InventoryApp inventoryApp;

	@Autowired
	private LoadManifestApp loadManifestApp;

	@Autowired
	private LoadReturnApp loadReturnApp;

	@Autowired
	private BillableApp orderConfirmationApp;

	@Autowired
	private BillableApp deliveryReturnApp;

	@Autowired
	private PaymentApp paymentApp;

	@Autowired
	private PickListApp pickListApp;

	@Autowired
	private BillableApp orderReturnApp;

	@Autowired
	private RemittanceApp remittanceApp;

	@Autowired
	private RemittanceVarianceApp remittanceVarianceApp;

	@Autowired
	private StockTakeApp stockTakeApp;

	@Autowired
	private StockTakeVarianceApp stockTakeVarianceApp;

	@Autowired
	private ItemApp itemApp;

	@Autowired
	private RouteApp routeApp;

	@Autowired
	private StyleApp styleDialog;

	@Autowired
	private TruckApp truckApp;

	@Autowired
	private UserApp userDialog;

	@Override
	protected GridPane addGridPaneNodes(GridPane gp) {
		//TODO
		gp.add(button(deliveryListApp, deliveryListButton, "deliveryList"), 0, 0);
		gp.add(button(loadManifestApp, loadManifestButton, "loadManifest"), 1, 0);
		gp.add(button(orderConfirmationApp, orderConfirmationButton, "orderConfirmation"), 2, 0);
		gp.add(button(bookingVarianceApp, bookingVarianceButton, "bookingVariance"), 3, 0);
		gp.add(button(pickListApp, pickListButton, "loadOut"), 4, 0);
		gp.add(button(orderReturnApp, orderReturnButton, "receivingReport"), 5, 0);
		gp.add(button(loadReturnApp, loadReturnButton, "loadIn"), 6, 0);

		gp.add(label.menu("DDL"), 0, 1);
		gp.add(label.menu("L/M"), 1, 1);
		gp.add(label.menu("OCS"), 2, 1);
		gp.add(label.menu("OCS Recon"), 3, 1);
		gp.add(label.menu("Load Out"), 4, 1);
		gp.add(label.menu("OCS Return"), 5, 1);
		gp.add(label.menu("Load In"), 6, 1);

		gp.add(button(badRmaApp, badRmaButton, "badOrder"), 0, 2);
		gp.add(button(blanketBalanceApp, blanketBalanceButton, "blanketBalance"), 1, 2);
		gp.add(button(remittanceApp, remittanceButton, "remittance"), 2, 2);
		gp.add(button(remittanceVarianceApp, remittanceVarianceButton, "remittanceVariance"), 3, 2);
		//TODO
		gp.add(button(deliveryReturnApp, deliveryReturnButton, "deliveryReturn"), 4, 2);
		gp.add(button(paymentApp, paymentButton, "payment"), 5, 2);
		gp.add(button(upApp.type(UPLOAD), upButton, "upload"), 6, 2);
		gp.add(button(downApp.type(DOWNLOAD), downButton, "download"), 7, 2);

		gp.add(label.menu("Bad Order"), 0, 3);
		gp.add(label.menu("Blanket OCS"), 1, 3);
		gp.add(label.menu("Remittance"), 2, 3);
		gp.add(label.menu("Remit Recon"), 3, 3);
		gp.add(label.menu("DDL Return "), 4, 3);
		gp.add(label.menu("Payment"), 5, 3);
		gp.add(label.menu(upload()), 6, 3);
		gp.add(label.menu(download()), 7, 3);

		gp.add(button(inventoryApp, inventoryButton, "inventory"), 0, 4);
		gp.add(button(stockTakeApp, stockTakeButton, "stockTake"), 1, 4);
		gp.add(button(stockTakeVarianceApp, stockTakeVarianceButton, "stockTakeVariance"), 2, 4);
		gp.add(button(itemApp, itemButton, "item"), 3, 4);
		gp.add(button(routeApp, routeButton, "route"), 4, 4);
		gp.add(button(truckApp, truckButton, "truck"), 5, 4);
		gp.add(button(userDialog, userButton, "user"), 6, 4);
		gp.add(button(styleDialog, styleButton, "style"), 7, 4);

		gp.add(label.menu("Inventory"), 0, 5);
		gp.add(label.menu("Stock Take"), 1, 5);
		gp.add(label.menu("Stock Recon"), 2, 5);
		gp.add(label.menu("Item"), 3, 5);
		gp.add(label.menu("Route"), 4, 5);
		gp.add(label.menu("Truck"), 5, 5);
		gp.add(label.menu("User"), 6, 5);
		gp.add(label.menu("UI Style"), 7, 5);
		return gp;
	}
}
