package ph.txtdis.fx.tab;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javafx.beans.binding.BooleanBinding;
import javafx.beans.value.ObservableBooleanValue;
import javafx.scene.Node;
import ph.txtdis.fx.pane.CustomerPane;

@Scope("prototype")
@Component("customerTab")
public class CustomerTab extends AbstractTab {

	@Autowired
	private CustomerPane customerPane;

	public CustomerTab() {
		super("Basic Information");
	}

	public void disableNameFieldIf(ObservableBooleanValue b) {
		customerPane.disableNameFieldIf(b);
	}

	public BooleanBinding hasIncompleteData() {
		return customerPane.hasIncompleteData();
	}

	@Override
	public void refresh() {
		customerPane.refresh();
	}

	@Override
	public void save() {
		customerPane.save();
	}

	@Override
	public void select() {
		super.select();
		customerPane.select();
	}

	public BooleanBinding showsPartnerAsACustomer() {
		return customerPane.showsPartnerAsACustomer();
	}

	@Override
	protected List<Node> mainVerticalPaneNodes() {
		return customerPane.mainVerticalPaneNodes();
	}

	@Override
	protected void setBindings() {
		customerPane.setBindings();
	}

	@Override
	protected void setListeners() {
		customerPane.setListeners();
	}
}
