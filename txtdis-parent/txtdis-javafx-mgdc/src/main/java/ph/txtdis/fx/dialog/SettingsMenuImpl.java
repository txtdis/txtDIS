package ph.txtdis.fx.dialog;

import static javafx.stage.Modality.WINDOW_MODAL;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import ph.txtdis.app.ChannelApp;
import ph.txtdis.app.CustomerApp;
import ph.txtdis.app.HolidayApp;
import ph.txtdis.app.ItemApp;
import ph.txtdis.app.ItemFamilyApp;
import ph.txtdis.app.ItemTreeApp;
import ph.txtdis.app.PricingTypeApp;
import ph.txtdis.app.RouteApp;
import ph.txtdis.app.TruckApp;
import ph.txtdis.app.WarehouseApp;
import ph.txtdis.fx.control.AppButton;
import ph.txtdis.util.FontIcon;

@Lazy
@Component("settingsMenu")
public class SettingsMenuImpl extends AbstractMenu implements SettingsMenu {

	@Autowired
	private AppButton channelButton, customerButton, holidayButton, itemButton, familyButton, treeButton,
			pricingTypeButton, routeButton, styleButton, truckButton, userButton, warehouseButton;

	@Autowired
	private ChannelApp channelApp;

	@Autowired
	private CustomerApp customerApp;

	@Autowired
	private HolidayApp holidayApp;

	@Autowired
	private ItemApp itemApp;

	@Autowired
	private ItemFamilyApp familyApp;

	@Autowired
	private ItemTreeApp treeApp;

	@Autowired
	private PricingTypeApp pricingTypeApp;

	@Autowired
	private RouteApp routeApp;

	@Autowired
	private StyleApp styleDialog;

	@Autowired
	private TruckApp truckApp;

	@Autowired
	private UserApp userDialog;

	@Autowired
	private WarehouseApp warehouseApp;

	@Override
	public SettingsMenuImpl addParent(Stage stage) {
		initOwner(stage);
		initModality(WINDOW_MODAL);
		return this;
	}

	@Override
	public void refresh() {
	}

	@Override
	public void setFocus() {
		requestFocus();
	}

	@Override
	public void start() {
		getIcons().add(new FontIcon("\ue92a"));
		setTitle(credentialService.username() + "@" + modulePrefix + " Settings Menu");
		setScene(createScene());
		show();
	}

	@Override
	protected GridPane addGridPaneNodes(GridPane gp) {
		gp.add(button(itemApp, itemButton, "item"), 0, 0);
		gp.add(button(familyApp, familyButton, "itemFamily"), 1, 0);
		gp.add(button(treeApp, treeButton, "itemTree"), 2, 0);
		gp.add(button(pricingTypeApp, pricingTypeButton, "pricingType"), 3, 0);
		gp.add(button(warehouseApp, warehouseButton, "warehouse"), 4, 0);
		gp.add(button(holidayApp, holidayButton, "holiday"), 5, 0);

		gp.add(label.menu("Item Master"), 0, 1);
		gp.add(label.menu("Item Family"), 1, 1);
		gp.add(label.menu("Item Tree"), 2, 1);
		gp.add(label.menu("Pricing Type"), 3, 1);
		gp.add(label.menu("Warehouses"), 4, 1);
		gp.add(label.menu("Holidays"), 5, 1);

		gp.add(button(truckApp, truckButton, "truck"), 0, 2);
		gp.add(button(routeApp, routeButton, "route"), 1, 2);
		gp.add(button(channelApp, channelButton, "channel"), 2, 2);
		gp.add(button(customerApp, customerButton, "customer"), 3, 2);
		gp.add(button(userDialog.addParent(this), userButton, "user"), 4, 2);
		gp.add(button(styleDialog.addParent(this), styleButton, "style"), 5, 2);

		gp.add(label.menu("Trucks"), 0, 3);
		gp.add(label.menu("Routes"), 1, 3);
		gp.add(label.menu("Channels"), 2, 3);
		gp.add(label.menu("Customers"), 3, 3);
		gp.add(label.menu("Users"), 4, 3);
		gp.add(label.menu("Styles"), 5, 3);

		return gp;
	}
}
