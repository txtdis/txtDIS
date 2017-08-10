package ph.txtdis.dyvek.fx.dialog;

import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import ph.txtdis.app.LaunchableApp;
import ph.txtdis.app.RemittanceApp;
import ph.txtdis.dyvek.app.*;
import ph.txtdis.dyvek.service.ReminderService;
import ph.txtdis.fx.control.AppButton;
import ph.txtdis.fx.dialog.AbstractMenu;
import ph.txtdis.fx.dialog.MessageDialog;
import ph.txtdis.info.Information;
import ph.txtdis.type.ModuleType;
import ph.txtdis.util.FontIcon;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

import static ph.txtdis.type.ModuleType.*;
import static ph.txtdis.util.UserUtils.username;

@Scope("prototype")
@Component("tradingMenu")
public class TradingMenuImpl
	extends AbstractMenu
	implements TradingMenu {

	@Autowired
	private MessageDialog dialog;

	@Autowired
	private AppButton salesButton, deliveryButton, clientBillAssignmentButton, clientBillingButton, remittanceButton,
		purchaseButton, vendorBillingButton, cashAdvanceButton, remoteStockButton, expenseButton;

	@Autowired
	private SalesApp salesApp;

	@Autowired
	private DeliveryApp deliveryApp;

	@Autowired
	private ClientBillAssignmentApp clientBillAssignmentApp;

	@Autowired
	private ClientBillingApp clientBillingApp;

	@Autowired
	private RemittanceApp remittanceApp;

	@Autowired
	private PurchaseApp purchaseApp;

	@Autowired
	private VendorBillingApp vendorBillingApp;

	@Autowired
	private CashAdvanceApp cashAdvanceApp;

	@Autowired
	private RemoteStockApp remoteStockApp;

	@Autowired
	private ExpenseApp expenseApp;

	@Autowired
	private ReminderService service;

	private Map<ModuleType, ToDoApp> appMap;

	@Override
	protected GridPane addGridPaneNodes(GridPane gp) {

		gp.add(button(salesApp, salesButton, "salesOrder"), 0, 0);
		gp.add(button(deliveryApp, deliveryButton, "deliveryReport"), 1, 0);
		gp.add(button(clientBillAssignmentApp, clientBillAssignmentButton, "clientBillAssignment"), 2, 0);
		gp.add(button(clientBillingApp, clientBillingButton, "clientBill"), 3, 0);
		gp.add(button(remittanceApp, remittanceButton, "collection"), 4, 0);

		gp.add(label.menu("Cust P/O"), 0, 1);
		gp.add(label.menu("Deliveries"), 1, 1);
		gp.add(label.menu("P/O of D/R"), 2, 1);
		gp.add(label.menu("Cust Billing"), 3, 1);
		gp.add(label.menu("Payments"), 4, 1);

		gp.add(button(purchaseApp, purchaseButton, "purchaseOrder"), 0, 2);
		gp.add(button(vendorBillingApp, vendorBillingButton, "vendorBill"), 1, 2);
		gp.add(button(cashAdvanceApp, cashAdvanceButton, "cashAdvance"), 2, 2);
		gp.add(button(remoteStockApp, remoteStockButton, "remoteStock"), 3, 2);
		gp.add(button(expenseApp, expenseButton, "expense"), 4, 2);

		gp.add(label.menu("Suppl P/O"), 0, 3);
		gp.add(label.menu("Suppl Billing"), 1, 3);
		gp.add(label.menu("Cash Adv"), 2, 3);
		gp.add(label.menu("Satellite"), 3, 3);
		gp.add(label.menu("Expenses"), 4, 3);

		return gp;
	}

	@Override
	public TradingMenu addParent(Stage stage) {
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
		/*
		createModuleAppMap();
		service.setThingsToDo();
		getToDo();
		*/
	}

	@Override
	public void initialize() {
		getIcons().add(new FontIcon("\ue954"));
		setTitle(username() + "@" + modulePrefix + " Trading Menu");
		setScene(createScene());
	}

	@SuppressWarnings("unused")
	private void createModuleAppMap() {
		appMap = new HashMap<>();
		appMap.put(DELIVERY_TO_SALES_ORDER, clientBillAssignmentApp);
		appMap.put(SALES_BILLING, clientBillingApp);
		appMap.put(PURCHASE_ORDER, purchaseApp);
		appMap.put(DELIVERY_TO_PURCHASE_ORDER, vendorBillingApp);
		appMap.entrySet().stream().forEach(a -> a.getValue().setOnHidden(e -> getToDo()));
	}

	private void getToDo() {
		try {
			service.checkThingToDo();
		} catch (Information i) {
			showReminder(i);
		} catch (NoSuchElementException e) {
			removeOnHiddenHandler();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void showReminder(Information i) {
		dialog.showOption(i.getMessage(), "Take Action", "Ignore");
		dialog.setOnOptionSelection(e -> takeAction());
		dialog.setOnDefaultSelection(e -> ignore());
		dialog.start();
	}

	private void removeOnHiddenHandler() {
		appMap.entrySet().stream().forEach(a -> a.getValue().removeOnHidden());
	}

	private void takeAction() {
		dialog.close();
		startApp(service.getThingToDo());
	}

	private void ignore() {
		dialog.close();
		service.ignore();
	}

	private void startApp(List<String> l) {
		LaunchableApp app = appMap.get(ModuleType.valueOf(l.get(0)));
		app.start();
		app.actOn(l.get(1), "");
		app.refresh();
	}
}
