package ph.txtdis.mgdc.fx.tab;

import javafx.beans.binding.BooleanBinding;
import javafx.beans.value.ObservableBooleanValue;
import javafx.scene.Node;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import ph.txtdis.fx.pane.CustomerPane;
import ph.txtdis.fx.tab.AbstractTab;

import java.util.List;

@Scope("prototype")
@Component("customerTab")
public class CustomerTabImpl //
	extends AbstractTab //
	implements CustomerTab {

	@Autowired
	private CustomerPane customerPane;

	public CustomerTabImpl() {
		super("Basic Information");
	}

	@Override
	public void clear() {
		customerPane.clear();
	}

	@Override
	public void disableNameFieldIf(ObservableBooleanValue b) {
		customerPane.disableNameFieldIf(b);
	}

	@Override
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

	@Override
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
	public void setFocus() {
		customerPane.setFocus();
	}

	@Override
	protected void setListeners() {
		customerPane.setListeners();
	}
}
