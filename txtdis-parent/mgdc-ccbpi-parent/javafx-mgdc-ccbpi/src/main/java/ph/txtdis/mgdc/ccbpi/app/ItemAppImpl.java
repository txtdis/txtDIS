package ph.txtdis.mgdc.ccbpi.app;

import javafx.scene.Node;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import ph.txtdis.app.AbstractKeyedApp;
import ph.txtdis.app.Searchable;
import ph.txtdis.fx.control.AppButton;
import ph.txtdis.fx.dialog.SearchDialog;
import ph.txtdis.fx.pane.PricedItemPane;
import ph.txtdis.mgdc.app.LaunchableItemApp;
import ph.txtdis.mgdc.ccbpi.dto.Item;
import ph.txtdis.mgdc.ccbpi.service.BommedDiscountedPricedValidatedItemService;

import java.util.ArrayList;
import java.util.List;

import static java.util.Arrays.asList;

@Scope("prototype")
@Component("itemApp")
public class ItemAppImpl //
	extends AbstractKeyedApp<BommedDiscountedPricedValidatedItemService, Item, Long, Long> //
	implements LaunchableItemApp,
	Searchable<Item> {

	@Autowired
	private ItemListApp itemListApp;

	@Autowired
	private PricedItemPane itemPane;

	@Autowired
	private AppButton searchButton;

	@Autowired
	private SearchDialog searchDialog;

	@Override
	protected List<AppButton> addButtons() {
		List<AppButton> l = new ArrayList<>(super.addButtons());
		l.add(searchButton.icon("search").tooltip("Search...").build());
		return l;
	}

	@Override
	protected void clear() {
		super.clear();
		itemPane.clear();
	}

	@Override
	public void goToDefaultFocus() {
		newButton.requestFocus();
	}

	@Override
	protected List<Node> mainVerticalPaneNodes() {
		return asList(itemPane.get(), trackedPane());
	}

	@Override
	public void nextFocus() {
		refresh();
	}

	@Override
	public void refresh() {
		super.refresh();
		itemPane.refresh();
	}

	@Override
	protected void save() {
		itemPane.save();
		super.save();
	}

	@Override
	protected void setBindings() {
		super.setBindings();
		itemPane.setBindings();
		saveButton.disableIf(itemPane.hasIncompleteData());
	}

	@Override
	protected void setListeners() {
		super.setListeners();
		itemPane.setListeners();
		searchButton.onAction(e -> openSearchDialog(service, this, itemListApp, messageDialog, searchDialog));
	}

	@Override
	public void updateUponVerification(Item item) throws Exception {
		service.openByDoubleClickedTableCellId(item.getId());
	}
}
