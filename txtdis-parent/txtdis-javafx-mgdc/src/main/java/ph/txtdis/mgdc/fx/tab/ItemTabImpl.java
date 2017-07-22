package ph.txtdis.mgdc.fx.tab;

import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javafx.beans.binding.BooleanBinding;
import javafx.scene.Node;
import ph.txtdis.fx.pane.BommedItemPane;
import ph.txtdis.fx.tab.AbstractTab;

@Scope("prototype")
@Component("itemTab")
public class ItemTabImpl //
		extends AbstractTab //
		implements ItemTab {

	@Autowired
	private BommedItemPane itemPane;

	public ItemTabImpl() {
		super("Basic Information");
	}

	@Override
	public void clear() {
		itemPane.clear();
	}

	@Override
	public BooleanBinding hasIncompleteData() {
		return itemPane.hasIncompleteData();
	}

	@Override
	public BooleanBinding needsPrice() {
		return itemPane.needsPrice();
	}

	@Override
	public void refresh() {
		itemPane.refresh();
	}

	@Override
	public void save() {
		itemPane.save();
	}

	@Override
	public void select() {
		super.select();
		itemPane.select();
	}

	@Override
	protected List<Node> mainVerticalPaneNodes() {
		return Arrays.asList(itemPane.get());
	}

	@Override
	protected void setBindings() {
		itemPane.setBindings();
	}

	@Override
	protected void setListeners() {
		itemPane.setListeners();
	}

	@Override
	public void setFocus() {
		itemPane.setFocus();
	}
}
