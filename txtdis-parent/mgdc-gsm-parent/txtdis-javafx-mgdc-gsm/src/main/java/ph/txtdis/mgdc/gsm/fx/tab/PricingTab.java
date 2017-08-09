package ph.txtdis.mgdc.gsm.fx.tab;

import javafx.beans.binding.BooleanBinding;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.layout.VBox;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import ph.txtdis.fx.tab.AbstractTab;
import ph.txtdis.mgdc.gsm.fx.table.PricingTable;
import ph.txtdis.mgdc.gsm.service.BommedDiscountedPricedValidatedItemService;

import java.util.List;

import static java.util.Arrays.asList;

@Scope("prototype")
@Component("pricingTab")
public class PricingTab
	extends AbstractTab {

	@Autowired
	private BommedDiscountedPricedValidatedItemService service;

	@Autowired
	private PricingTable table;

	public PricingTab() {
		super("Pricing");
	}

	@Override
	public void clear() {
		table.removeListener();
	}

	public BooleanBinding hasNoPrices() {
		return table.isEmpty();
	}

	@Override
	public void refresh() {
		table.items(service.getPriceList());
	}

	@Override
	public void save() {
		service.setPriceList(table.getItems());
	}

	@Override
	protected VBox mainVerticalPane() {
		VBox vbox = super.mainVerticalPane();
		vbox.setAlignment(Pos.CENTER);
		return vbox;
	}

	@Override
	protected List<Node> mainVerticalPaneNodes() {
		return asList(pane.centeredHorizontal(table.build()));
	}

	@Override
	protected void setBindings() {
	}

	@Override
	protected void setListeners() {
	}
}
