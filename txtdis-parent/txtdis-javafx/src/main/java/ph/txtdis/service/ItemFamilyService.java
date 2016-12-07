package ph.txtdis.service;

import java.util.List;

import ph.txtdis.dto.Item;
import ph.txtdis.dto.ItemFamily;
import ph.txtdis.info.Information;
import ph.txtdis.type.ItemTier;

public interface ItemFamilyService
		extends Listed<ItemFamily>, SavedByName<ItemFamily>, Titled, UniquelyNamed<ItemFamily> {

	List<ItemFamily> getItemAncestry(Item item) throws Exception;

	List<ItemFamily> listItemFamily(ItemTier t) throws Exception;

	List<ItemFamily> listItemParents() throws Exception;

	ItemFamily save(String name, ItemTier tier) throws Information, Exception;

	boolean isOffSite();
}