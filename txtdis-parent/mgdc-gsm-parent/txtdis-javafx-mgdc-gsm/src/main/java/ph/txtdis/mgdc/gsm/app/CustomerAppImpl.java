package ph.txtdis.mgdc.gsm.app;

import static java.util.Arrays.asList;

import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import ph.txtdis.app.AbstractMasterApp;
import ph.txtdis.fx.tab.InputTab;
import ph.txtdis.info.Information;
import ph.txtdis.mgdc.app.LaunchableCustomerApp;
import ph.txtdis.mgdc.fx.tab.CustomerTab;
import ph.txtdis.mgdc.gsm.dto.Customer;
import ph.txtdis.mgdc.gsm.fx.dialog.CustomerDiscountTab;
import ph.txtdis.mgdc.gsm.fx.tab.CreditTab;
import ph.txtdis.mgdc.gsm.service.CustomerService;
import ph.txtdis.mgdc.gsm.service.Qualified_CreditAndDiscountGivenCustomerService;
import ph.txtdis.util.ClientTypeMap;

@Scope("prototype")
@Component("customerApp")
public class CustomerAppImpl //
		extends AbstractMasterApp<CustomerService, Customer> //
		implements LaunchableCustomerApp {

	@Autowired
	private CustomerTab customerTab;

	@Autowired
	private CustomerDiscountTab discountTab;

	@Autowired
	private CustomerListApp customerListApp;

	@Autowired
	private CreditTab creditTab;

	@Autowired
	private Qualified_CreditAndDiscountGivenCustomerService qualified_CreditAndDiscountGivenCustomerService;

	@Autowired
	private ClientTypeMap map;

	private BooleanProperty cannotReactivate, creditAndOrDiscountsConnotBeGiven, noChangesNeedingApproval;

	private List<InputTab> inputTabs;

	@Override
	protected void clear() {
		super.clear();
		inputTabs.forEach(t -> t.clear());
		goToDefaultFocus();
	}

	@Override
	public void goToDefaultFocus() {
		customerTab.select();
	}

	@Override
	protected void listSearchResults() throws Exception {
		customerListApp.addParent(this).start();
		Customer c = customerListApp.getSelection();
		if (c != null)
			service.openByDoubleClickedTableCellId(c.getId());
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
		return inputTabs().stream().map(t -> t.asTab()).collect(Collectors.toList());
	}

	private List<InputTab> inputTabs() {
		return inputTabs = asList( //
				customerTab.build(), //
				creditTab.build(), //
				discountTab.build());
	}

	@Override
	public void refresh() {
		inputTabs.forEach(t -> t.refresh());
		creditAndOrDiscountsConnotBeGiven.set(qualified_CreditAndDiscountGivenCustomerService.cannotGiveCreditAndOrDiscount());
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
			dialog.show(i).addParent(this).start();
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
		inputTabs.forEach(t -> t.save());
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
}
