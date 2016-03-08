package ph.txtdis.app;

import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import ph.txtdis.dto.Item;
import ph.txtdis.fx.AppSelectable;
import ph.txtdis.fx.table.ItemTable;
import ph.txtdis.service.ItemService;

@Lazy
@Component("itemApp")
public class ItemApp extends AbstractTableApp<ItemTable, ItemService, Item> implements AppSelectable<Item> {

	@Override
	public Item getSelection() {
		return table.getItem();
	}
}
