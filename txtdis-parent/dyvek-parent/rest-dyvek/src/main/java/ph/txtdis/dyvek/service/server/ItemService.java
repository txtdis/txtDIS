package ph.txtdis.dyvek.service.server;

import ph.txtdis.dyvek.domain.ItemEntity;
import ph.txtdis.dyvek.model.Item;
import ph.txtdis.service.SavedNameListService;

public interface ItemService //
		extends SavedNameListService<Item> {

	ItemEntity findEntityByName(String item);
}
