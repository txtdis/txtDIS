package ph.txtdis.mgdc.ccbpi.fx.dialog;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javafx.scene.layout.GridPane;
import ph.txtdis.app.ItemApp;
import ph.txtdis.app.PickListApp;
import ph.txtdis.app.RemittanceApp;
import ph.txtdis.app.StockTakeApp;
import ph.txtdis.app.StockTakeVarianceApp;
import ph.txtdis.app.TruckAppImpl;
import ph.txtdis.fx.control.AppButtonImpl;
import ph.txtdis.fx.control.LabelFactory;
import ph.txtdis.fx.dialog.AbstractMainMenu;
import ph.txtdis.fx.dialog.StyleApp;
import ph.txtdis.fx.dialog.UserApp;
import ph.txtdis.mgdc.app.BadRmaApp;
import ph.txtdis.mgdc.app.InventoryApp;
import ph.txtdis.mgdc.app.RouteApp;
import ph.txtdis.mgdc.ccbpi.app.BlanketBalanceApp;
import ph.txtdis.mgdc.ccbpi.app.BookingVarianceApp;
import ph.txtdis.mgdc.ccbpi.app.DeliveryListApp;
import ph.txtdis.mgdc.ccbpi.app.DeliveryReturnApp;
import ph.txtdis.mgdc.ccbpi.app.LoadManifestApp;
import ph.txtdis.mgdc.ccbpi.app.LoadReturnApp;
import ph.txtdis.mgdc.ccbpi.app.OrderConfirmationApp;
import ph.txtdis.mgdc.ccbpi.app.OrderReturnApp;
import ph.txtdis.mgdc.ccbpi.app.PaymentApp;
import ph.txtdis.mgdc.ccbpi.app.RemittanceVarianceApp;

@Scope("prototype")
@Component("mainMenu")
public class MainMenuImpl //
		extends AbstractMainMenu {

	@Autowired
	private AppButtonImpl deliveryListButton, loadManifestButton, orderConfirmationButton, pickListButton, orderReturnButton, loadReturnButton,
			bookingVarianceButton, //
			badRmaButton, blanketBalanceButton, remittanceButton, remittanceVarianceButton, deliveryReturnButton, paymentButton, inventoryButton, // 
			stockTakeButton, stockTakeVarianceButton, itemButton, routeButton, truckButton, userButton, styleButton;

	@Autowired
	private LabelFactory label;

	@Autowired
	private BadRmaApp badRmaApp;

	@Autowired
	private BlanketBalanceApp blanketBalanceApp;

	@Autowired
	private BookingVarianceApp bookingVarianceApp;

	@Autowired
	private DeliveryListApp deliveryListApp;

	@Autowired
	private DeliveryReturnApp deliveryReturnApp;

	@Autowired
	private InventoryApp inventoryApp;

	@Autowired
	private LoadManifestApp loadManifestApp;

	@Autowired
	private LoadReturnApp loadReturnApp;

	@Autowired
	private OrderConfirmationApp orderConfirmationApp;

	@Autowired
	private OrderReturnApp orderReturnApp;

	@Autowired
	private PaymentApp paymentApp;

	@Autowired
	private PickListApp pickListApp;

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
	private TruckAppImpl truckApp;

	@Autowired
	private UserApp userDialog;

	@Override
	protected GridPane addGridPaneNodes(GridPane gp) {
		gp.add(button(deliveryListApp, deliveryListButton, "deliveryList"), 0, 0);
		gp.add(button(loadManifestApp, loadManifestButton, "loadManifest"), 1, 0);
		gp.add(button(orderConfirmationApp, orderConfirmationButton, "orderConfirmation"), 2, 0);
		gp.add(button(pickListApp, pickListButton, "loadOut"), 3, 0);
		gp.add(button(orderReturnApp, orderReturnButton, "orderReturn"), 4, 0);
		gp.add(button(loadReturnApp, loadReturnButton, "loadIn"), 5, 0);
		gp.add(button(bookingVarianceApp, bookingVarianceButton, "bookingVariance"), 6, 0);

		gp.add(label.menu("DDL"), 0, 1);
		gp.add(label.menu("L/M"), 1, 1);
		gp.add(label.menu("OCS"), 2, 1);
		gp.add(label.menu("Load Out"), 3, 1);
		gp.add(label.menu("R/R"), 4, 1);
		gp.add(label.menu("Load In"), 5, 1);
		gp.add(label.menu("OCS Recon"), 6, 1);

		gp.add(button(badRmaApp, badRmaButton, "badRma"), 0, 2);
		gp.add(button(blanketBalanceApp, blanketBalanceButton, "blanketBalance"), 1, 2);
		gp.add(button(remittanceApp, remittanceButton, "remittance"), 2, 2);
		gp.add(button(remittanceVarianceApp, remittanceVarianceButton, "remittanceVariance"), 3, 2);
		gp.add(button(deliveryReturnApp, deliveryReturnButton, "deliveryReturn"), 4, 2);
		gp.add(button(paymentApp, paymentButton, "payment"), 5, 2);
		gp.add(button(inventoryApp, inventoryButton, "inventory"), 6, 2);

		gp.add(label.menu("Bad Order"), 0, 3);
		gp.add(label.menu("Blanket Bal"), 1, 3);
		gp.add(label.menu("Remittance"), 2, 3);
		gp.add(label.menu("Remit Recon"), 3, 3);
		gp.add(label.menu("DD Return "), 4, 3);
		gp.add(label.menu("DD Payment"), 5, 3);
		gp.add(label.menu("Inventory"), 6, 3);

		gp.add(button(stockTakeApp, stockTakeButton, "stockTake"), 0, 4);
		gp.add(button(stockTakeVarianceApp, stockTakeVarianceButton, "stockTakeVariance"), 1, 4);
		gp.add(button(itemApp, itemButton, "item"), 2, 4);
		gp.add(button(routeApp, routeButton, "route"), 3, 4);
		gp.add(button(truckApp, truckButton, "truck"), 4, 4);
		gp.add(button(userDialog, userButton, "user"), 5, 4);
		gp.add(button(styleDialog, styleButton, "style"), 6, 4);

		gp.add(label.menu("Stock Take"), 0, 5);
		gp.add(label.menu("Stock Recon"), 1, 5);
		gp.add(label.menu("Item"), 2, 5);
		gp.add(label.menu("Route"), 3, 5);
		gp.add(label.menu("Truck"), 4, 5);
		gp.add(label.menu("User"), 5, 5);
		gp.add(label.menu("UI Style"), 6, 5);
		return gp;
	}
}
