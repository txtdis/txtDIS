package ph.txtdis.mgdc.gsm.app;

import org.springframework.beans.factory.annotation.Autowired;

import ph.txtdis.app.AbstractMasterApp;
import ph.txtdis.app.ItemApp;
import ph.txtdis.mgdc.gsm.dto.Item;
import ph.txtdis.mgdc.gsm.service.BommedDiscountedPricedValidatedItemService;

public abstract class AbstractItemApp //
		extends AbstractMasterApp<BommedDiscountedPricedValidatedItemService, Item> //
		implements ItemApp {

	@Autowired
	private ItemListApp itemListApp;

	@Override
	protected void listSearchResults() throws Exception {
		itemListApp.addParent(this).start();
		Item i = itemListApp.getSelection();
		if (i != null)
			service.openByDoubleClickedTableCellId(i.getId());
	}
}
