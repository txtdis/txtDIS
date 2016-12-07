package ph.txtdis.service;

import static org.apache.log4j.Logger.getLogger;

import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ph.txtdis.domain.ItemTreeEntity;
import ph.txtdis.dto.ItemFamily;
import ph.txtdis.dto.ItemTree;
import ph.txtdis.repository.ItemTreeRepository;

@Service("itemTreeService")
public class ItemTreeServiceImpl extends AbstractIdService<ItemTreeRepository, ItemTreeEntity, ItemTree, Long>
		implements ItemTreeService {

	private static Logger logger = getLogger(ItemTreeServiceImpl.class);

	@Autowired
	private PrimaryItemFamilyService familyService;

	@Override
	public ItemTree find(ItemFamily family, ItemFamily parent) {
		ItemTreeEntity e = repository.findByFamilyIdAndParentId(family.getId(), parent.getId());
		return toDTO(e);
	}

	@Override
	public List<ItemTree> list() {
		List<ItemTreeEntity> l = repository.findByOrderByFamilyAscParentAsc();
		return toList(l);
	}

	@Override
	protected ItemTree toDTO(ItemTreeEntity e) {
		if (e == null)
			return null;
		ItemTree t = new ItemTree();
		t.setId(e.getId());
		t.setFamily(familyService.toDTO(e.getFamily()));
		t.setParent(familyService.toDTO(e.getParent()));
		t.setCreatedBy(e.getCreatedBy());
		t.setCreatedOn(e.getCreatedOn());
		return t;
	}

	@Override
	protected ItemTreeEntity toEntity(ItemTree t) {
		if (t == null)
			return null;
		ItemTreeEntity e = repository.findByFamilyIdAndParentId(t.getFamily().getId(), t.getParent().getId());
		return e != null ? e : newEntity(t);
	}

	protected ItemTreeEntity newEntity(ItemTree t) {
		ItemTreeEntity e;
		e = new ItemTreeEntity();
		e.setFamily(familyService.toEntity(t.getFamily()));
		e.setParent(familyService.toEntity(t.getParent()));
		return e;
	}

	@Override
	public ItemTree findByFamily(ItemFamily family) {
		ItemTreeEntity e = family == null ? null : repository.findByFamilyId(family.getId());
		return e == null ? null : toDTO(e);
	}

	@Override
	public List<ItemTree> listByParent(String parent) {
		List<ItemTreeEntity> trees = repository.findByParentNameOrderByFamilyNameAsc(parent);
		return toList(trees);
	}

	@Override
	public void save(ItemFamily child, ItemFamily parent) {
		ItemTree t = new ItemTree();
		t.setFamily(child);
		t.setParent(parent);
		save(t);
		logger.info("\n    Tree: " + child + ", " + parent);
	}
}