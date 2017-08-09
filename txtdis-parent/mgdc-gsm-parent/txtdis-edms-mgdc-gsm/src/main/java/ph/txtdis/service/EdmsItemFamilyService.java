package ph.txtdis.service;

import ph.txtdis.dto.ItemFamily;

import java.util.List;

public interface EdmsItemFamilyService {

	List<ItemFamily> findBrands(String category);

	List<ItemFamily> findCategories(String clazz);

	List<ItemFamily> findClasses();

	ItemFamily toBrand(String name);

	ItemFamily toClass(String name);
}
