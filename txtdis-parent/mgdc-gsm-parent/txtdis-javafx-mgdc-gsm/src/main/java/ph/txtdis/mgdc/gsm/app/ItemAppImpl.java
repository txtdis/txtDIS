package ph.txtdis.mgdc.gsm.app;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.scene.Node;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import ph.txtdis.app.SelectableListApp;
import ph.txtdis.fx.tab.InputTab;
import ph.txtdis.mgdc.app.LaunchableItemApp;
import ph.txtdis.mgdc.fx.tab.ItemTab;
import ph.txtdis.mgdc.gsm.dto.Item;
import ph.txtdis.mgdc.gsm.fx.tab.PricingTab;
import ph.txtdis.mgdc.gsm.service.BommedDiscountedPricedValidatedItemService;

import java.util.List;

import static java.util.Arrays.asList;
import static java.util.stream.Collectors.toList;

@Scope("prototype")
@Component("itemApp")
public class ItemAppImpl //
	extends AbstractMasterApp<BommedDiscountedPricedValidatedItemService, Item> //
	implements LaunchableItemApp {

	@Autowired
	protected PricingTab priceTab;

	protected List<InputTab> inputTabs;

	@Autowired
	private ItemListApp itemListApp;

	@Autowired
	private ItemTab itemTab;

	private BooleanProperty noChangesNeedingApproval;

	@Override
	protected void clear() {
		super.clear();
		inputTabs.forEach(t -> t.clear());
		goToDefaultFocus();
	}

	@Override
	public void goToDefaultFocus() {
		itemTab.select();
	}

	@Override
	protected SelectableListApp<Item> getSelectableListApp() {
		return itemListApp;
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

	private List<InputTab> inputTabs() {
		return inputTabs = asList(itemTab.build(), priceTab.build());
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

	@Override
	public void updateUponVerification(Item item) throws Exception {
		service.openByDoubleClickedTableCellId(item.getId());
	}
}
