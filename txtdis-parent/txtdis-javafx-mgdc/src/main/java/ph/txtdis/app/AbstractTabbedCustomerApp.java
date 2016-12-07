package ph.txtdis.app;

import static java.util.Arrays.asList;
import static java.util.stream.Collectors.toList;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.scene.Node;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import ph.txtdis.fx.tab.CreditTab;
import ph.txtdis.fx.tab.CustomerDiscountTab;
import ph.txtdis.fx.tab.CustomerTab;
import ph.txtdis.fx.tab.InputTab;

public abstract class AbstractTabbedCustomerApp extends AbstractCustomerApp {

	@Autowired
	private CustomerTab customerTab;

	@Autowired
	private CustomerDiscountTab discountTab;

	@Autowired
	protected CreditTab creditTab;

	private BooleanProperty noChangesNeedingApproval;

	protected List<InputTab> inputTabs;

	@Override
	public void refresh() {
		inputTabs.forEach(t -> t.refresh());
		super.refresh();
	}

	@Override
	public void save() {
		inputTabs.forEach(t -> t.save());
		super.save();
	}

	@Override
	public void setFocus() {
		customerTab.select();
	}

	protected void checkForChangesNeedingApproval(Tab tab) {
		if (tab.isSelected()) {
			boolean b = service.noChangesNeedingApproval(tab.getText());
			noChangesNeedingApproval.set(b);
		}
	}

	protected List<InputTab> inputTabs() {
		return inputTabs = new ArrayList<>(asList(customerTab.build(), creditTab.build(), discountTab.build()));
	}

	private List<Tab> tabs() {
		return inputTabs().stream().map(t -> t.asTab()).collect(toList());
	}

	@Override
	protected List<Node> mainVerticalPaneNodes() {
		List<Node> l = new ArrayList<>(asList(tabPane()));
		l.addAll(super.mainVerticalPaneNodes());
		return l;
	}

	protected TabPane tabPane() {
		TabPane p = new TabPane();
		p.setStyle("-fx-tab-min-width: 80;");
		p.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);
		p.getTabs().setAll(tabs());
		return p;
	}

	@Override
	protected void setBindings() {
		noChangesNeedingApproval = new SimpleBooleanProperty(true);
		customerTab.disableNameFieldIf(createdOnDisplay.isNotEmpty());
		creditTab.disableIf(customerTab.showsPartnerAsACustomer().not());
		discountTab.disableIf(creditTab.disabledProperty());
		super.setBindings();
	}

	@Override
	protected void setDecisionAndSaveButtonBindings() {
		decisionButton.disableIf(notPosted()//
				.or(noChangesNeedingApproval));
		saveButton.disableIf(isAlreadyDeactivated()//
				.or(customerTab.hasIncompleteData())//
				.or(isOffSite));
	}

	@Override
	protected void setListeners() {
		super.setListeners();
		customerTab.setOnSelectionChanged(e -> noChangesNeedingApproval.set(true));
		creditTab.setOnSelectionChanged(e -> checkForChangesNeedingApproval(creditTab));
		discountTab.setOnSelectionChanged(e -> checkForChangesNeedingApproval(discountTab));
	}
}
