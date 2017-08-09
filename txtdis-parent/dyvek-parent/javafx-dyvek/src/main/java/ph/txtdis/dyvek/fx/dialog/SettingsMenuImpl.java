package ph.txtdis.dyvek.fx.dialog;

import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import org.springframework.beans.factory.annotation.Lookup;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import ph.txtdis.app.App;
import ph.txtdis.app.ItemApp;
import ph.txtdis.app.TruckApp;
import ph.txtdis.dyvek.app.BankApp;
import ph.txtdis.dyvek.app.TradingClientApp;
import ph.txtdis.dyvek.app.TruckingClientApp;
import ph.txtdis.dyvek.app.VendorApp;
import ph.txtdis.fx.dialog.AbstractMenu;
import ph.txtdis.fx.dialog.SettingsMenu;
import ph.txtdis.fx.dialog.StyleApp;
import ph.txtdis.fx.dialog.UserApp;
import ph.txtdis.util.FontIcon;

import static ph.txtdis.util.UserUtils.username;

@Scope("prototype")
@Component("settingsMenu")
public class SettingsMenuImpl
	extends AbstractMenu //
	implements SettingsMenu {

	private static final String ITEM = "item";

	private static final String BANK = "bank";

	private static final String SUPPLIER = "supplier";

	private static final String CUSTOMER = "customer";

	private static final String TRUCK = "truck";

	private static final String TRUCK_CLIENT = "truckClient";

	private static final String USER = "user";

	private static final String STYLE = "style";

	@Override
	protected GridPane addGridPaneNodes(GridPane gp) {
		gp.add(button(ITEM), 0, 0);
		gp.add(button(BANK), 1, 0);
		gp.add(button(SUPPLIER), 2, 0);
		gp.add(button(CUSTOMER), 3, 0);

		gp.add(label.menu("Item"), 0, 1);
		gp.add(label.menu("Bank"), 1, 1);
		gp.add(label.menu("Supplier"), 2, 1);
		gp.add(label.menu("Trade Cust"), 3, 1);

		gp.add(button(TRUCK), 0, 2);
		gp.add(button(TRUCK_CLIENT), 1, 2);
		gp.add(button(USER), 2, 2);
		gp.add(button(STYLE), 3, 2);

		gp.add(label.menu("Truck"), 0, 3);
		gp.add(label.menu("Truck Cust"), 1, 3);
		gp.add(label.menu("User"), 2, 3);
		gp.add(label.menu("UI Style"), 3, 3);

		return gp;
	}

	@Override
	public SettingsMenu addParent(Stage stage) {
		return this;
	}

	@Override
	public void refresh() {
		goToDefaultFocus();
	}

	@Override
	public void goToDefaultFocus() {
		requestFocus();
	}

	@Override
	public void start() {
		initialize();
		show();
	}

	@Override
	public void initialize() {
		getIcons().add(new FontIcon("\ue92a"));
		setTitle(username() + "@" + modulePrefix + ": Settings Menu");
		setScene(createScene());
	}

	@Override
	protected App getApp(String icon) {
		switch (icon) {
			case ITEM:
				return itemApp();
			case BANK:
				return bankApp();
			case SUPPLIER:
				return vendorApp();
			case CUSTOMER:
				return tradeClientApp();
			case TRUCK:
				return truckApp();
			case TRUCK_CLIENT:
				return truckClientApp();
			case USER:
				return userApp();
			case STYLE:
				return styleApp();
			default:
				return null;
		}
	}

	@Lookup
	ItemApp itemApp() {
		return null;
	}

	@Lookup
	BankApp bankApp() {
		return null;
	}

	@Lookup
	VendorApp vendorApp() {
		return null;
	}

	@Lookup
	TradingClientApp tradeClientApp() {
		return null;
	}

	@Lookup
	TruckApp truckApp() {
		return null;
	}

	@Lookup
	TruckingClientApp truckClientApp() {
		return null;
	}

	@Lookup
	UserApp userApp() {
		return null;
	}

	@Lookup
	StyleApp styleApp() {
		return null;
	}
}
