package ph.txtdis.mgdc.ccbpi.app;

import static java.util.Arrays.asList;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javafx.scene.Node;
import javafx.scene.layout.HBox;
import ph.txtdis.app.AbstractMasterApp;
import ph.txtdis.fx.control.AppButtonImpl;
import ph.txtdis.fx.pane.PricedItemPane;
import ph.txtdis.mgdc.app.LaunchableItemApp;
import ph.txtdis.mgdc.ccbpi.dto.Item;
import ph.txtdis.mgdc.ccbpi.service.BommedDiscountedPricedValidatedItemService;

@Scope("prototype")
@Component("itemApp")
public class ItemAppImpl //
		extends AbstractMasterApp<BommedDiscountedPricedValidatedItemService, Item> //
		implements LaunchableItemApp {

	@Autowired
	private ItemListApp itemListApp;

	@Autowired
	private PricedItemPane itemPane;

	@Override
	protected void clear() {
		super.clear();
		itemPane.clear();
	}

	@Override
	protected List<AppButtonImpl> addButtons() {
		List<AppButtonImpl> l = new ArrayList<>(super.addButtons());
		l.removeAll(Arrays.asList(deOrReActivationButton, decisionButton));
		return l;
	}

	@Override
	public void refresh() {
		super.refresh();
		itemPane.refresh();
	}

	@Override
	public void goToDefaultFocus() {
		newButton.requestFocus();
	}

	@Override
	protected void listSearchResults() throws Exception {
		itemListApp.addParent(this).start();
		Item i = itemListApp.getSelection();
		if (i != null)
			service.openByDoubleClickedTableCellId(i.getId());
	}

	@Override
	protected List<Node> mainVerticalPaneNodes() {
		return asList(itemPane.get(), trackedPane(), deactivationPane());
	}

	@Override
	protected HBox trackedPane() {
		List<Node> l = new ArrayList<>(creationNodes());
		l.addAll(lastModificationNodes());
		return box.forHorizontalPane(l);
	}

	private Node deactivationPane() {
		return box.forHorizontalPane(deactivationNodes());
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
	}

	@Override
	protected void setListeners() {
		super.setListeners();
		itemPane.setListeners();
	}

	@Override
	protected void setSaveButtonBinding() {
		saveButton.disableIf(itemPane.hasIncompleteData());
	}
}
