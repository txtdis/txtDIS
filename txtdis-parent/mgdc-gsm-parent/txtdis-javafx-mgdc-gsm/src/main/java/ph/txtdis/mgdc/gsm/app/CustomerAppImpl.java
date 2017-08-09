package ph.txtdis.mgdc.gsm.app;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import ph.txtdis.app.SelectableListApp;
import ph.txtdis.fx.tab.InputTab;
import ph.txtdis.info.Information;
import ph.txtdis.mgdc.app.LaunchableCustomerApp;
import ph.txtdis.mgdc.fx.tab.CustomerTab;
import ph.txtdis.mgdc.gsm.dto.Customer;
import ph.txtdis.mgdc.gsm.fx.dialog.CustomerDiscountTab;
import ph.txtdis.mgdc.gsm.fx.tab.CreditTab;
import ph.txtdis.mgdc.gsm.service.CustomerService;
import ph.txtdis.util.ClientTypeMap;

import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.List;

import static java.util.Arrays.asList;
import static java.util.stream.Collectors.toList;

@Scope("prototype")
@Component("customerApp")
public class CustomerAppImpl //
	extends AbstractMasterApp<CustomerService, Customer> //
	implements LaunchableCustomerApp {

	private final CustomerTab customerTab;

	private final CustomerDiscountTab discountTab;

	private final CustomerListApp customerListApp;

	private final CreditTab creditTab;

	private final ClientTypeMap map;

	private BooleanProperty cannotReactivate, creditAndOrDiscountsConnotBeGiven, noChangesNeedingApproval;

	private List<InputTab> inputTabs;

	@Autowired
	public CustomerAppImpl(CustomerTab customerTab,
	                       CustomerDiscountTab discountTab,
	                       CustomerListApp customerListApp,
	                       CreditTab creditTab, ClientTypeMap map) {
		this.customerTab = customerTab;
		this.discountTab = discountTab;
		this.customerListApp = customerListApp;
		this.creditTab = creditTab;
		this.map = map;
	}

	@Override
	protected void clear() {
		super.clear();
		inputTabs.forEach(InputTab::clear);
		goToDefaultFocus();
	}

	@Override
	public void goToDefaultFocus() {
		customerTab.select();
	}

	@Override
	protected List<Node> mainVerticalPaneNodes() {
		return Arrays.asList(tabPane(), trackedPane());
	}

	private TabPane tabPane() {
		TabPane p = new TabPane();
		p.setStyle("-fx-tab-min-width: 80;");
		p.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);
		p.getTabs().setAll(tabs());
		return p;
	}

	private List<Tab> tabs() {
		return inputTabs().stream().map(InputTab::asTab).collect(toList());
	}

	private List<InputTab> inputTabs() {
		return inputTabs = asList(
			customerTab.build(),
			creditTab.build(),
			discountTab.build());
	}

	@Override
	protected SelectableListApp<Customer> getSelectableListApp() {
		return customerListApp;
	}

	@Override
	public void refresh() {
		inputTabs.forEach(InputTab::refresh);
		creditAndOrDiscountsConnotBeGiven
			.set(service.cannotGiveCreditAndOrDiscount());
		cannotReactivate.set(service.cannotReactivate());
		refreshDeOrReActivationButton(service.getDeactivatedOn());
		super.refresh();
	}

	private void refreshDeOrReActivationButton(ZonedDateTime deactivatedOn) {
		if (deactivatedOn == null)
			setDeOrReActivationButton("deactivate", "Deactivate...", e -> deactivate());
		else
			setDeOrReActivationButton("reactivate", "Reactivate...", e -> reactivate());
	}

	private void setDeOrReActivationButton(String icon, String toolTip, EventHandler<ActionEvent> event) {
		deOrReActivationButton.setText(map.icon(icon));
		deOrReActivationButton.getTooltip().setText(toolTip);
		deOrReActivationButton.onAction(event);
	}

	private void reactivate() {
		try {
			service.reactivate();
		} catch (Exception e) {
			showErrorDialog(e);
		} catch (Information i) {
			messageDialog.show(i).addParent(this).start();
		} finally {
			refresh();
		}
	}

	@Override
	protected void renew() {
		super.renew();
		customerTab.setFocus();
	}

	@Override
	public void save() {
		inputTabs.forEach(InputTab::save);
		super.save();
	}

	@Override
	protected void setBindings() {
		noChangesNeedingApproval = new SimpleBooleanProperty(true);
		cannotReactivate = new SimpleBooleanProperty(true);
		customerTab.disableNameFieldIf(createdOnDisplay.isNotEmpty());
		creditTab.disableIf(customerTab.showsPartnerAsACustomer().not());
		discountTab.disableIf(creditTab.disabledProperty());
		creditAndOrDiscountsConnotBeGiven = new SimpleBooleanProperty(true);
		creditTab.disableIf(customerTab.showsPartnerAsACustomer().not() //
			.or(creditAndOrDiscountsConnotBeGiven));
		super.setBindings();
	}

	@Override
	protected void setDecisionButtonBinding() {
		decisionButton.disableIf(isNew()//
			.or(noChangesNeedingApproval));
	}

	@Override
	protected void setDeOrActivationButton() {
		deOrReActivationButton.disableIf(cannotReactivate);
	}

	@Override
	protected void setListeners() {
		super.setListeners();
		customerTab.setOnSelectionChanged(e -> noChangesNeedingApproval.set(true));
		creditTab.setOnSelectionChanged(e -> checkForChangesNeedingApproval(creditTab));
		discountTab.setOnSelectionChanged(e -> checkForChangesNeedingApproval(discountTab));
	}

	private void checkForChangesNeedingApproval(Tab tab) {
		if (tab.isSelected()) {
			boolean b = service.noChangesNeedingApproval(tab.getText());
			noChangesNeedingApproval.set(b);
		}
	}

	@Override
	protected void setSaveButtonBinding() {
		saveButton.disableIf(isAlreadyDeactivated()//
			.or(customerTab.hasIncompleteData()));//
	}

	@Override
	public void updateUponVerification(Customer c) throws Exception {
		service.openByDoubleClickedTableCellId(c.getId());
	}
}
