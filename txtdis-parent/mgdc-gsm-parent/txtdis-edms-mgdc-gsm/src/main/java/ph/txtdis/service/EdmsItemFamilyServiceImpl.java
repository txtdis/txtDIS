package ph.txtdis.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ph.txtdis.dto.ItemFamily;
import ph.txtdis.type.ItemTier;
import ph.txtdis.type.UomType;

import java.util.List;
import java.util.stream.Collectors;

@Service("itemFamilyService")
public class EdmsItemFamilyServiceImpl //
	implements EdmsItemFamilyService {

	@Autowired
	private EdmsItemService itemService;

	@Override
	public List<ItemFamily> findClasses() {
		List<String> classes = itemService.listClassNames();
		return classes.stream().map(s -> toClass(s)).collect(Collectors.toList());
	}

	@Override
	public ItemFamily toClass(String name) {
		return toFamily(name, ItemTier.CLASS);
	}

	private ItemFamily toFamily(String name, ItemTier t) {
		ItemFamily f = new ItemFamily();
		f.setName(name);
		f.setTier(t);
		f.setUom(UomType.CS);
		return f;
	}

	@Override
	public List<ItemFamily> findCategories(String clazz) {
		List<String> classes = itemService.listCategoryNames(clazz);
		return classes.stream().map(s -> toCategory(s)).collect(Collectors.toList());
	}

	private ItemFamily toCategory(String name) {
		return toFamily(name, ItemTier.CATEGORY);
	}

	@Override
	public List<ItemFamily> findBrands(String category) {
		List<String> classes = itemService.listBrandNames(category);
		return classes.stream().map(s -> toBrand(s)).collect(Collectors.toList());
	}

	@Override
	public ItemFamily toBrand(String name) {
		return toFamily(name, ItemTier.BRAND);
	}
}