package ph.txtdis.fx.dialog;

import static ph.txtdis.util.SpringUtil.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import static ph.txtdis.type.ModuleType.BOOKING;
import static ph.txtdis.type.ModuleType.PICK_LIST;
import static ph.txtdis.type.ModuleType.RECEIVING_REPORT;
import static ph.txtdis.type.ModuleType.RECONCILIATION;
import static ph.txtdis.type.ModuleType.SALES_ORDER;
import static ph.txtdis.type.ModuleType.SALES_RETURN;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import ph.txtdis.app.BankApp;
import ph.txtdis.app.CustomerApp;
import ph.txtdis.app.ImportApp;
import ph.txtdis.app.ItemApp;
import ph.txtdis.app.MultiTyped;
import ph.txtdis.app.RemittanceApp;
import ph.txtdis.app.SalesApp;
import ph.txtdis.app.SalesRevenueApp;
import ph.txtdis.app.Startable;
import ph.txtdis.fx.FontIcon;
import ph.txtdis.fx.StyleSheet;
import ph.txtdis.fx.control.AppButton;
import ph.txtdis.fx.control.LabelFactory;

@Lazy
@Component("mainMenu")
public class MainMenu extends Stage {

	@Autowired
	private BankApp bankApp;

	@Autowired
	private CustomerApp customerApp;

	@Autowired
	private ImportApp importApp;

	@Autowired
	private ItemApp itemApp;

	@Autowired
	private RemittanceApp remittanceApp;

	@Autowired
	private SalesApp bookingApp, assignmentApp, pickingApp, salesReturnApp, receivingApp, reconciliationApp;

	@Autowired
	private SalesRevenueApp salesRevenueApp;

	@Autowired
	private StyleApp styleDialog;

	@Autowired
	private UserApp userDialog;

	@Autowired
	private LabelFactory label;

	@Autowired
	private StyleSheet styleSheet;

	public void display() {
		getIcons().add(new FontIcon("\ue826"));
		setTitle("txtDIS Menu");
		setScene(createScene());
		styleSheet.update(user().getStyle());
		show();
	}

	private String appType(Startable app) {
		return ((MultiTyped) app).type();
	}

	private AppButton button() {
		return new AppButton().fontSize(44);
	}

	private AppButton button(Startable app) {
		AppButton button = buttonType(app).build();
		button.setOnAction(event -> app.start());
		return button;
	}

	private AppButton buttonType(Startable app) {
		AppButton button = button();
		if (app instanceof MultiTyped)
			return button.icon(appType(app));
		return button.app(app);
	}

	private Scene createScene() {
		Scene s = new Scene(dialogBox());
		s.getStylesheets().add("/css/base.css");
		return s;
	}

	private Parent dialogBox() {
		HBox b = new HBox(gridPane());
		b.setPadding(new Insets(10));
		b.setAlignment(Pos.CENTER);
		return b;
	}

	private GridPane gridPane() {
		GridPane gp = new GridPane();
		gp.setHgap(5);
		gp.setVgap(5);
		gp.setAlignment(Pos.CENTER);

		gp.add(button(importApp), 0, 0);
		gp.add(button(assignmentApp.type(SALES_ORDER)), 1, 0);
		gp.add(button(pickingApp.type(PICK_LIST)), 2, 0);
		gp.add(button(salesReturnApp.type(SALES_RETURN)), 3, 0);
		gp.add(button(receivingApp.type(RECEIVING_REPORT)), 4, 0);
		gp.add(button(remittanceApp), 5, 0);
		gp.add(button(reconciliationApp.type(RECONCILIATION)), 6, 0);

		gp.add(label.menu("Import"), 0, 1);
		gp.add(label.menu("Assign"), 1, 1);
		gp.add(label.menu("Pick"), 2, 1);
		gp.add(label.menu("Deduct"), 3, 1);
		gp.add(label.menu("Receive"), 4, 1);
		gp.add(label.menu("Remit"), 5, 1);
		gp.add(label.menu("Reconcile"), 6, 1);

		gp.add(button(bookingApp.type(BOOKING)), 0, 2);
		gp.add(button(salesRevenueApp), 1, 2);
		gp.add(button(bankApp), 2, 2);
		gp.add(button(customerApp), 3, 2);
		gp.add(button(itemApp), 4, 2);
		gp.add(button(userDialog.addParent(this)), 5, 2);
		gp.add(button(styleDialog.addParent(this)), 6, 2);

		gp.add(label.menu("S/O History"), 0, 3);
		gp.add(label.menu("Revenue"), 1, 3);
		gp.add(label.menu("Bank"), 2, 3);
		gp.add(label.menu("Outlet"), 3, 3);
		gp.add(label.menu("Item Master"), 4, 3);
		gp.add(label.menu("User"), 5, 3);
		gp.add(label.menu("U/I Setting"), 6, 3);
		return gp;
	}
}
