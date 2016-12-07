package ph.txtdis.app;

import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javafx.scene.Node;
import ph.txtdis.fx.pane.PricedItemPane;

@Scope("prototype")
@Component("itemApp")
public class ItemAppImpl extends AbstractItemApp {

	@Autowired
	private PricedItemPane itemPane;

	@Override
	public void refresh() {
		super.refresh();
		itemPane.refresh();
	}

	@Override
	public void setFocus() {
		newButton.requestFocus();
	}

	@Override
	protected List<Node> mainVerticalPaneNodes() {
		return Arrays.asList(itemPane.get(), trackedPane());
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
		saveButton.disableIf(itemPane.hasIncompleteData().or(isOffSite));
	}

	@Override
	protected void setListeners() {
		super.setListeners();
		itemPane.setListeners();
	}
}
