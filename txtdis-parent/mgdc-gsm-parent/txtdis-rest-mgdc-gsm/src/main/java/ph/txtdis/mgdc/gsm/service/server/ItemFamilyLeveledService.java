package ph.txtdis.mgdc.gsm.service.server;

import ph.txtdis.dto.ItemFamily;

import java.util.List;

public interface ItemFamilyLeveledService {

	List<ItemFamily> listAncestry(String family);

	List<ItemFamily> findParents();

	ItemFamily getParent(ItemFamily family);

	List<ItemFamily> findBrands(String category);

	List<ItemFamily> findCategories(String clazz);

	List<ItemFamily> findClasses();
}
