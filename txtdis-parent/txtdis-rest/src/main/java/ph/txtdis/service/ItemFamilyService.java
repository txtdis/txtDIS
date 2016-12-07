package ph.txtdis.service;

import java.util.List;

import ph.txtdis.dto.ItemFamily;

public interface ItemFamilyService extends NameListCreateService<ItemFamily> {

	List<ItemFamily> listAncestry(String family);

	List<ItemFamily> findParents();

	ItemFamily getParent(ItemFamily family);

	List<ItemFamily> findBrands(String category);

	List<ItemFamily> findCategories(String clazz);

	List<ItemFamily> findClasses();
}