package ph.txtdis.mgdc.gsm.service.server;

import static java.util.Arrays.asList;
import static org.apache.log4j.Logger.getLogger;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ph.txtdis.dto.ItemFamily;
import ph.txtdis.dto.ItemTree;
import ph.txtdis.mgdc.domain.ItemFamilyEntity;
import ph.txtdis.mgdc.service.server.AbstractItemFamilyService;
import ph.txtdis.service.ReadOnlyService;
import ph.txtdis.type.ItemTier;

@Service("itemFamilyService")
public class ItemFamilyServiceImpl //
		extends AbstractItemFamilyService //
		implements ImportedLeveledItemFamilyService {

	private static Logger logger = getLogger(ItemFamilyServiceImpl.class);

	@Autowired
	private ItemTreeService treeService;

	@Autowired
	private ReadOnlyService<ItemFamily> readOnlyService;

	@Override
	public List<ItemFamily> findBrands(String category) {
		return findByParentName(category);
	}

	private List<ItemFamily> findByParentName(String category) {
		List<ItemTree> trees = treeService.listByParent(category);
		return trees.stream().map(t -> t.getFamily()).collect(Collectors.toList());
	}

	@Override
	public List<ItemFamily> findCategories(String clazz) {
		return findByParentName(clazz);
	}

	@Override
	public List<ItemFamily> findParents() {
		return findByTier(ItemTier.BRAND);
	}

	private List<ItemFamily> findByTier(ItemTier tier) {
		List<ItemFamilyEntity> l = repository.findByTierOrderByNameAsc(tier);
		return toModels(l);
	}

	@Override
	public List<ItemFamily> findClasses() {
		return findByTier(ItemTier.CLASS);
	}

	@Override
	public ItemFamily getParent(ItemFamily family) {
		ItemTree tree = treeService.findByFamily(family);
		return tree == null ? null : tree.getParent();
	}

	@Override
	public void importAll() throws Exception {
		ItemFamily liquor = save(list("/classes").get(0));
		List<ItemFamily> categories = createCategoryClassTrees(liquor);
		createBrandCategoryTrees(categories);
	}

	@Override
	public List<ItemFamily> listAncestry(String familyName) {
		ItemFamily family = toModel(repository.findByNameIgnoreCase(familyName));
		return getAncentryByTraversingTree(family);
	}

	private List<ItemFamily> getAncentryByTraversingTree(ItemFamily family) {
		List<ItemFamily> list = new ArrayList<>(asList(family));
		while ((family = getParent(family)) != null)
			list.add(family);
		return list;
	}

	@Override
	public ItemFamily save(ItemFamily l) {
		ItemFamilyEntity e = toEntity(l);
		e = repository.save(e);
		logger.info("\n\t\t\t\tItemFamily: " + e.getName() + " - " + e.getTier());
		return toModel(e);
	}

	private List<ItemFamily> list(String endPoint) throws Exception {
		return readOnlyService.module("itemFamily").getList(endPoint);
	}

	private List<ItemFamily> createCategoryClassTrees(ItemFamily liquor) throws Exception {
		List<ItemFamily> categories = list("/categories?of=" + liquor.getName());
		repository.save(toEntities(categories));
		categories.forEach(category -> treeService.save(category, liquor));
		return categories;
	}

	private void createBrandCategoryTrees(List<ItemFamily> categories) throws Exception {
		for (ItemFamily category : categories)
			for (ItemFamily brand : list("/brands?of=" + category.getName()))
				treeService.save(save(brand), category);
	}
}