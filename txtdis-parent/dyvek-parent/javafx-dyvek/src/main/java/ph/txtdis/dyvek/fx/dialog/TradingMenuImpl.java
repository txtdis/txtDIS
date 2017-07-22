package ph.txtdis.dyvek.fx.dialog;

import static ph.txtdis.type.ModuleType.DELIVERY_TO_PURCHASE_ORDER;
import static ph.txtdis.type.ModuleType.DELIVERY_TO_SALES_ORDER;
import static ph.txtdis.type.ModuleType.PURCHASE_ORDER;
import static ph.txtdis.type.ModuleType.SALES_BILLING;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import ph.txtdis.app.LaunchableApp;
import ph.txtdis.app.RemittanceApp;
import ph.txtdis.dyvek.app.CashAdvanceApp;
import ph.txtdis.dyvek.app.ClientBillAssignmentApp;
import ph.txtdis.dyvek.app.ClientBillingApp;
import ph.txtdis.dyvek.app.DeliveryApp;
import ph.txtdis.dyvek.app.ExpenseApp;
import ph.txtdis.dyvek.app.PurchaseApp;
import ph.txtdis.dyvek.app.RemoteStockApp;
import ph.txtdis.dyvek.app.SalesApp;
import ph.txtdis.dyvek.app.ToDoApp;
import ph.txtdis.dyvek.app.VendorBillingApp;
import ph.txtdis.dyvek.service.ReminderService;
import ph.txtdis.fx.control.AppButtonImpl;
import ph.txtdis.fx.dialog.AbstractMenu;
import ph.txtdis.fx.dialog.MessageDialog;
import ph.txtdis.info.Information;
import ph.txtdis.type.ModuleType;
import ph.txtdis.util.FontIcon;

@Scope("prototype")
@Component("tradingMenu")
public class TradingMenuImpl //
		extends AbstractMenu //
		implements TradingMenu {

	@Autowired
	private MessageDialog dialog;

	@Autowired
	private AppButtonImpl salesButton, deliveryButton, clientBillAssignmentButton, clientBillingButton, remittanceButton, //
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
		gp.add(label.menu("D/R to S/O"), 2, 1);
		gp.add(label.menu("Cust Billing"), 3, 1);
		gp.add(label.menu("Cust Pymt"), 4, 1);

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
	public void goToDefaultFocus() {
		requestFocus();
	}

	@Override
	public void initialize() {
		getIcons().add(new FontIcon("\ue954"));
		setTitle(credentialService.username() + "@" + modulePrefix + " Trading Menu");
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
		//createModuleAppMap();
		//service.setThingsToDo();
		//getToDo();
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

	private void ignore() {
		dialog.close();
		service.ignore();
	}

	private void removeOnHiddenHandler() {
		appMap.entrySet().stream().forEach(a -> a.getValue().removeOnHidden());
	}

	private void startApp(List<String> l) {
		LaunchableApp app = appMap.get(ModuleType.valueOf(l.get(0)));
		app.start();
		app.actOn(l.get(1), "");
		app.refresh();
	}

	private void takeAction() {
		dialog.close();
		startApp(service.getThingToDo());
	}
}
