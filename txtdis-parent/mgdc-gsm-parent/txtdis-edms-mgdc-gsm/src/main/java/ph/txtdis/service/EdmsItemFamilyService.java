package ph.txtdis.service;

import java.util.List;

import ph.txtdis.dto.ItemFamily;

public interface EdmsItemFamilyService {

	List<ItemFamily> findBrands(String category);

	List<ItemFamily> findCategories(String clazz);

	List<ItemFamily> findClasses();

	ItemFamily toBrand(String name);

	ItemFamily toClass(String name);
}
