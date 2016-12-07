package ph.txtdis.fx.tab;

import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javafx.beans.binding.BooleanBinding;
import javafx.scene.Node;
import ph.txtdis.fx.pane.BommedItemPane;

@Scope("prototype")
@Component("itemTab")
public class ItemTab extends AbstractTab {

	@Autowired
	private BommedItemPane itemPane;

	public ItemTab() {
		super("Basic Information");
	}

	public BooleanBinding hasIncompleteData() {
		return itemPane.hasIncompleteData();
	}

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
}
