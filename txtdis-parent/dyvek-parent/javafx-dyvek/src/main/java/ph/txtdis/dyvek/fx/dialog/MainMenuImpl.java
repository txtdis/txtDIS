package ph.txtdis.dyvek.fx.dialog;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javafx.scene.layout.GridPane;
import ph.txtdis.dyvek.app.BankReconApp;
import ph.txtdis.fx.control.AppButtonImpl;
import ph.txtdis.fx.dialog.AbstractMainMenu;
import ph.txtdis.fx.dialog.SettingsMenu;

@Scope("prototype")
@Component("mainMenu")
public class MainMenuImpl //
		extends AbstractMainMenu {

	@Autowired
	private AppButtonImpl tradingButton, truckingButton, bankReconButton, settingsButton;

	@Autowired
	private TradingMenu tradingMenu;

	@Autowired
	private TruckingMenu truckingMenu;

	@Autowired
	private BankReconApp bankReconApp;

	@Autowired
	private SettingsMenu settingsMenu;

	@Override
	protected GridPane addGridPaneNodes(GridPane gp) {

		gp.add(button(tradingMenu, tradingButton, "trading"), 0, 0);
		gp.add(button(truckingMenu, truckingButton, "truck"), 1, 0);
		gp.add(button(bankReconApp, bankReconButton, "bankRecon"), 2, 0);
		gp.add(button(settingsMenu, settingsButton, "settings"), 3, 0);

		gp.add(label.menu("Trading"), 0, 1);
		gp.add(label.menu("Trucking"), 1, 1);
		gp.add(label.menu("Bank Recon"), 2, 1);
		gp.add(label.menu("Settings"), 3, 1);

		return gp;
	}
}
