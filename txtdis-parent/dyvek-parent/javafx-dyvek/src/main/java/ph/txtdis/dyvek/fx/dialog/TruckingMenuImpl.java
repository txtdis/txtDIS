package ph.txtdis.dyvek.fx.dialog;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import ph.txtdis.app.RemittanceApp;
import ph.txtdis.dyvek.app.ClientBillingApp;
import ph.txtdis.dyvek.app.CommissionApp;
import ph.txtdis.dyvek.app.ExpenseApp;
import ph.txtdis.dyvek.app.TruckLogApp;
import ph.txtdis.fx.control.AppButtonImpl;
import ph.txtdis.fx.dialog.AbstractMenu;
import ph.txtdis.util.FontIcon;

@Scope("prototype")
@Component("truckingMenu")
public class TruckingMenuImpl //
		extends AbstractMenu //
		implements TruckingMenu {

	@Autowired
	private AppButtonImpl truckLogButton, commissionButton, clientBillingButton, remittanceButton, expenseButton;

	@Autowired
	private TruckLogApp truckLogApp;

	@Autowired
	private CommissionApp commissionApp;

	@Autowired
	private ClientBillingApp clientBillingApp;

	@Autowired
	private RemittanceApp remittanceApp;

	@Autowired
	private ExpenseApp expenseApp;

	@Override
	protected GridPane addGridPaneNodes(GridPane gp) {

		gp.add(button(truckLogApp, truckLogButton, "truckLog"), 0, 0);
		gp.add(button(commissionApp, commissionButton, "commission"), 1, 0);
		gp.add(button(clientBillingApp, clientBillingButton, "clientBill"), 2, 0);
		gp.add(button(remittanceApp, remittanceButton, "collection"), 3, 0);
		gp.add(button(expenseApp, expenseButton, "expense"), 4, 0);

		gp.add(label.menu("Logs"), 0, 1);
		gp.add(label.menu("Commission"), 1, 1);
		gp.add(label.menu("Billing"), 2, 1);
		gp.add(label.menu("Payment"), 3, 1);
		gp.add(label.menu("Expenses"), 4, 1);

		return gp;
	}

	@Override
	public TruckingMenu addParent(Stage stage) {
		return this;
	}

	@Override
	public void goToDefaultFocus() {
		requestFocus();
	}

	@Override
	public void initialize() {
		getIcons().add(new FontIcon("\ue950"));
		setTitle(credentialService.username() + "@" + modulePrefix + " Trucking Menu");
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
