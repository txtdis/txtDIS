package ph.txtdis.mgdc.gsm.service.server;

import ph.txtdis.mgdc.gsm.dto.Item;

public interface ImportedItemService //
	extends ItemService,
	Imported {

	Item saveToEdms(Item i) throws Exception;
}
