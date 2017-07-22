package ph.txtdis.dyvek.service;

import java.util.List;

import ph.txtdis.dyvek.model.Item;
import ph.txtdis.info.Information;
import ph.txtdis.service.ListedAndResetableService;
import ph.txtdis.service.TitleAndHeaderAndIconAndModuleNamedAndTypeMappedService;
import ph.txtdis.service.UniqueNamedService;

public interface ItemService //
		extends ListedAndResetableService<Item>, TitleAndHeaderAndIconAndModuleNamedAndTypeMappedService, UniqueNamedService<Item> {

	public List<String> listNames();

	public Item save(String name) throws Information, Exception;
}
