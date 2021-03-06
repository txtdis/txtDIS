package ph.txtdis.mgdc.gsm.fx.dialog;

import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.layout.VBox;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import ph.txtdis.fx.tab.AbstractTab;
import ph.txtdis.mgdc.gsm.fx.table.CustomerDiscountTable;
import ph.txtdis.mgdc.gsm.service.ValueBasedCustomerDiscountService;

import java.util.List;

import static java.util.Arrays.asList;

@Scope("prototype")
@Component("customerDiscountTab")
public class CustomerDiscountTab
	extends AbstractTab {

	@Autowired
	private ValueBasedCustomerDiscountService service;

	@Autowired
	private CustomerDiscountTable customerDiscountTable;

	public CustomerDiscountTab() {
		super("Customer Discount");
	}

	@Override
	public void clear() {
		customerDiscountTable.removeListener();
	}

	@Override
	public void refresh() {
		customerDiscountTable.items(service.getCustomerDiscounts());
	}

	@Override
	public void save() {
		service.setCustomerDiscounts(customerDiscountTable.getItems());
	}

	@Override
	protected VBox mainVerticalPane() {
		VBox vbox = super.mainVerticalPane();
		vbox.setAlignment(Pos.CENTER);
		return vbox;
	}

	@Override
	protected List<Node> mainVerticalPaneNodes() {
		return asList(pane.centeredHorizontal(customerDiscountTable.build()));
	}

	@Override
	protected void setBindings() {
	}

	@Override
	protected void setListeners() {
	}
}
