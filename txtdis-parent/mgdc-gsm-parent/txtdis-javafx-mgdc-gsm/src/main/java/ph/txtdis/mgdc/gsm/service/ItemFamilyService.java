package ph.txtdis.mgdc.gsm.service;

import ph.txtdis.dto.ItemFamily;
import ph.txtdis.info.Information;
import ph.txtdis.mgdc.gsm.dto.Item;
import ph.txtdis.service.ListedAndResettableService;
import ph.txtdis.service.SavedByName;
import ph.txtdis.service.TitleAndHeaderAndIconAndModuleNamedAndTypeMappedService;
import ph.txtdis.service.UniqueNamedService;
import ph.txtdis.type.ItemTier;

import java.util.List;

public interface ItemFamilyService //
	extends ListedAndResettableService<ItemFamily>,
	SavedByName<ItemFamily>,
	TitleAndHeaderAndIconAndModuleNamedAndTypeMappedService,
	UniqueNamedService<ItemFamily> {

	List<ItemFamily> getItemAncestry(Item item) throws Exception;

	List<ItemFamily> listItemFamily(ItemTier t) throws Exception;

	List<ItemFamily> listItemParents() throws Exception;

	ItemFamily save(String name, ItemTier tier) throws Information, Exception;
}