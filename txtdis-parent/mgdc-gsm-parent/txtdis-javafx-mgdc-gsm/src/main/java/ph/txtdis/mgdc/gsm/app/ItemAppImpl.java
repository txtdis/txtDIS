package ph.txtdis.mgdc.gsm.app;

import static java.util.Arrays.asList;
import static java.util.stream.Collectors.toList;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.scene.Node;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import ph.txtdis.fx.tab.InputTab;
import ph.txtdis.mgdc.app.LaunchableItemApp;
import ph.txtdis.mgdc.fx.tab.ItemTab;
import ph.txtdis.mgdc.gsm.fx.tab.PricingTab;

@Scope("prototype")
@Component("itemApp")
public class ItemAppImpl //
		extends AbstractItemApp //
		implements LaunchableItemApp {

	@Autowired
	private ItemTab itemTab;

	@Autowired
	protected PricingTab priceTab;

	private BooleanProperty noChangesNeedingApproval;

	protected List<InputTab> inputTabs;

	@Override
	protected void clear() {
		super.clear();
		inputTabs.forEach(t -> t.clear());
		goToDefaultFocus();
	}

	@Override
	public void refresh() {
		inputTabs.forEach(t -> t.refresh());
		super.refresh();
	}

	@Override
	protected void renew() {
		super.renew();
		itemTab.setFocus();
	}

	@Override
	public void save() {
		inputTabs.forEach(t -> t.save());
		super.save();
	}

	@Override
	public void goToDefaultFocus() {
		itemTab.select();
	}

	private List<InputTab> inputTabs() {
		return inputTabs = asList(itemTab.build(), priceTab.build());
	}

	@Override
	protected List<Node> mainVerticalPaneNodes() {
		return asList(tabPane(), trackedPane());
	}

	private TabPane tabPane() {
		TabPane p = new TabPane();
		p.setStyle("-fx-tab-min-width: 80;");
		p.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);
		p.getTabs().addAll(tabs());
		return p;
	}

	private List<Tab> tabs() {
		return inputTabs().stream().map(t -> t.asTab()).collect(toList());
	}

	@Override
	protected void setBindings() {
		noChangesNeedingApproval = new SimpleBooleanProperty(true);
		super.setBindings();
		priceTab.disableIf(itemTab.hasIncompleteData());
	}

	@Override
	protected void setDecisionButtonBinding() {
		decisionButton.disableIf(isNew()//
				.or(noChangesNeedingApproval));
	}

	@Override
	protected void setListeners() {
		super.setListeners();
		itemTab.setOnSelectionChanged(e -> noChangesNeedingApproval.set(true));
		priceTab.setOnSelectionChanged(e -> checkForChangesNeedingApproval(priceTab));
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
				.or(itemTab.needsPrice().and(priceTab.hasNoPrices()))//
				.or(itemTab.hasIncompleteData()));
	}
}
