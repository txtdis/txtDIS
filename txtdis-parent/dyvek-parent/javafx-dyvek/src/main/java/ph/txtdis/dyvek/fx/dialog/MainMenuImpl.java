package ph.txtdis.dyvek.fx.dialog;

import javafx.scene.layout.GridPane;
import org.springframework.beans.factory.annotation.Lookup;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import ph.txtdis.app.App;
import ph.txtdis.dyvek.app.BankReconApp;
import ph.txtdis.fx.dialog.AbstractMainMenu;
import ph.txtdis.fx.dialog.SettingsMenu;

@Scope("prototype")
@Component("mainMenu")
public class MainMenuImpl
	extends AbstractMainMenu {

	private static final String TRADING = "trading";

	private static final String TRUCK = "truck";

	private static final String BANK_RECON = "bankRecon";

	private static final String SETTINGS = "settings";

	@Override
	protected GridPane addGridPaneNodes(GridPane gp) {

		gp.add(button(TRADING), 0, 0);
		gp.add(button(TRUCK), 1, 0);
		gp.add(button(BANK_RECON), 2, 0);
		gp.add(button(SETTINGS), 3, 0);

		gp.add(label.menu("Trading"), 0, 1);
		gp.add(label.menu("Trucking"), 1, 1);
		gp.add(label.menu("Bank Recon"), 2, 1);
		gp.add(label.menu("Settings"), 3, 1);

		return gp;
	}

	@Override
	protected App getApp(String icon) {
		switch (icon) {
			case TRADING:
				return tradingMenu();
			case TRUCK:
				return truckingMenu();
			case BANK_RECON:
				return bankReconApp();
			case SETTINGS:
				return settingsMenu();
			default:
				return null;
		}
	}

	@Lookup
	TradingMenu tradingMenu() {
		return null;
	}

	@Lookup
	TruckingMenu truckingMenu() {
		return null;
	}

	@Lookup
	BankReconApp bankReconApp() {
		return null;
	}

	@Lookup
	SettingsMenu settingsMenu() {
		return null;
	}
}
