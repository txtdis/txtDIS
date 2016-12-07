package ph.txtdis.service;

import static java.util.Arrays.asList;
import static org.apache.log4j.Logger.getLogger;
import static ph.txtdis.type.ItemTier.LINE;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import ph.txtdis.domain.ItemFamilyEntity;
import ph.txtdis.dto.ItemFamily;
import ph.txtdis.dto.ItemTree;
import ph.txtdis.repository.ItemFamilyRepository;
import ph.txtdis.type.ItemTier;

public abstract class AbstractItemFamilyService extends
		AbstractCreateNameListService<ItemFamilyRepository, ItemFamilyEntity, ItemFamily> implements PrimaryItemFamilyService {

	private static Logger logger = getLogger(AbstractItemFamilyService.class);

	@Autowired
	protected ItemTreeService treeService;

	@Override
	public List<ItemFamily> listAncestry(String familyName) {
		logger.info("\n    Family: " + familyName);
		ItemFamily family = toDTO(repository.findByNameIgnoreCase(familyName));
		return getAncentryByTraversingTree(family);
	}

	private List<ItemFamily> getAncentryByTraversingTree(ItemFamily family) {
		List<ItemFamily> list = new ArrayList<>(asList(family));
		while ((family = getParent(family)) != null)
			list.add(family);
		return list;
	}

	@Override
	public List<ItemFamily> findParents() {
		return findByTier(LINE);
	}

	protected List<ItemFamily> findByTier(ItemTier tier) {
		List<ItemFamilyEntity> l = repository.findByTierOrderByNameAsc(tier);
		return toList(l);
	}

	@Override
	public ItemFamily toDTO(ItemFamilyEntity e) {
		if (e == null)
			return null;
		ItemFamily f = new ItemFamily();
		f.setId(e.getId());
		f.setName(e.getName());
		f.setTier(e.getTier());
		f.setUom(e.getUom());
		f.setCreatedBy(e.getCreatedBy());
		f.setCreatedOn(e.getCreatedOn());
		return f;
	}

	@Override
	public ItemFamilyEntity toEntity(ItemFamily t) {
		return t == null ? null : getEntity(t);
	}

	private ItemFamilyEntity getEntity(ItemFamily t) {
		ItemFamilyEntity e = findSavedEntity(t);
		return e != null ? e : newEntity(t);
	}

	private ItemFamilyEntity findSavedEntity(ItemFamily t) {
		if (t.getId() != null)
			return repository.findOne(t.getId());
		return repository.findByNameIgnoreCase(t.getName());
	}

	private ItemFamilyEntity newEntity(ItemFamily t) {
		ItemFamilyEntity e = new ItemFamilyEntity();
		e.setName(t.getName());
		e.setTier(t.getTier());
		e.setUom(t.getUom());
		return e;
	}

	@Override
	public ItemFamily getParent(ItemFamily family) {
		ItemTree tree = treeService.findByFamily(family);
		return tree == null ? null : tree.getParent();
	}

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
	public List<ItemFamily> findClasses() {
		return findByTier(ItemTier.CLASS);
	}
}