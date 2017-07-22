package ph.txtdis.dyvek.fx.dialog;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import ph.txtdis.app.ItemApp;
import ph.txtdis.app.TruckAppImpl;
import ph.txtdis.dyvek.app.BankApp;
import ph.txtdis.dyvek.app.TradingClientApp;
import ph.txtdis.dyvek.app.TruckingClientApp;
import ph.txtdis.dyvek.app.VendorApp;
import ph.txtdis.fx.control.AppButtonImpl;
import ph.txtdis.fx.dialog.AbstractMenu;
import ph.txtdis.fx.dialog.SettingsMenu;
import ph.txtdis.fx.dialog.StyleApp;
import ph.txtdis.fx.dialog.UserApp;
import ph.txtdis.util.FontIcon;

@Scope("prototype")
@Component("settingsMenu")
public class SettingsMenuImpl //
		extends AbstractMenu //
		implements SettingsMenu {

	@Autowired
	private AppButtonImpl itemButton, bankButton, vendorButton, tradeClientButton, truckButton, truckClientButton, userButton, styleButton;

	@Autowired
	private ItemApp itemApp;

	@Autowired
	private BankApp bankApp;

	@Autowired
	private VendorApp vendorApp;

	@Autowired
	private TradingClientApp tradeClientApp;

	@Autowired
	private TruckAppImpl truckApp;

	@Autowired
	private TruckingClientApp truckClientApp;

	@Autowired
	private UserApp userApp;

	@Autowired
	private StyleApp styleApp;

	@Override
	protected GridPane addGridPaneNodes(GridPane gp) {
		gp.add(button(itemApp, itemButton, "item"), 0, 0);
		gp.add(button(bankApp, bankButton, "bank"), 1, 0);
		gp.add(button(vendorApp, vendorButton, "supplier"), 2, 0);
		gp.add(button(tradeClientApp, tradeClientButton, "customer"), 3, 0);

		gp.add(label.menu("Item"), 0, 1);
		gp.add(label.menu("Bank"), 1, 1);
		gp.add(label.menu("Supplier"), 2, 1);
		gp.add(label.menu("Trade Cust"), 3, 1);

		gp.add(button(truckApp, truckButton, "truck"), 0, 2);
		gp.add(button(truckClientApp, truckClientButton, "truckClient"), 1, 2);
		gp.add(button(userApp, userButton, "user"), 2, 2);
		gp.add(button(styleApp, styleButton, "style"), 3, 2);

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
	public void goToDefaultFocus() {
		requestFocus();
	}

	@Override
	public void initialize() {
		getIcons().add(new FontIcon("\ue92a"));
		setTitle(credentialService.username() + "@" + modulePrefix + " Settings Menu");
		setScene(createScene());
	}

	@Override
	public void refresh() {
		goToDefaultFocus();
	}

	@Override
	public void start() {
		initialize();
		show();
	}
}
