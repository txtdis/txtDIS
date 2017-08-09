package ph.txtdis.mgdc.ccbpi.app;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import ph.txtdis.app.AbstractExcelApp;
import ph.txtdis.app.SelectableListApp;
import ph.txtdis.mgdc.ccbpi.dto.Item;
import ph.txtdis.mgdc.ccbpi.fx.table.ItemListTable;
import ph.txtdis.mgdc.ccbpi.service.BommedDiscountedPricedValidatedItemService;

@Scope("prototype")
@Component("itemListApp")
public class ItemListApp //
	extends AbstractExcelApp<ItemListTable, BommedDiscountedPricedValidatedItemService, Item> //
	implements SelectableListApp<Item> {

	@Override
	public Item getSelection() {
		return table.getItem();
	}

	@Override
	public void start() {
		setStage(mainVerticalPane());
		table.setItem(null);
		refresh();
		showAndWait();
	}

	@Override
	protected String getTitleText() {
		return getHeaderText();
	}

	@Override
	protected String getHeaderText() {
		return "Item List";
	}
}
