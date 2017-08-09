package ph.txtdis.mgdc.gsm.service.server;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ph.txtdis.dto.ItemFamily;
import ph.txtdis.dto.ItemTree;
import ph.txtdis.mgdc.gsm.domain.ItemTreeEntity;
import ph.txtdis.mgdc.gsm.repository.ItemTreeRepository;
import ph.txtdis.mgdc.service.server.ConvertibleItemFamilyService;
import ph.txtdis.service.AbstractSavedReferencedKeyedService;

import java.util.List;

@Service("itemTreeService")
public class ItemTreeServiceImpl //
	extends AbstractSavedReferencedKeyedService<ItemTreeRepository, ItemTreeEntity, ItemTree, Long> //
	implements ItemTreeService {

	@Autowired
	private ConvertibleItemFamilyService familyService;

	@Override
	public ItemTree find(ItemFamily family, ItemFamily parent) {
		ItemTreeEntity e = repository.findByFamilyIdAndParentId(family.getId(), parent.getId());
		return toModel(e);
	}

	@Override
	protected ItemTree toModel(ItemTreeEntity e) {
		if (e == null)
			return null;
		ItemTree t = new ItemTree();
		t.setId(e.getId());
		t.setFamily(familyService.toModel(e.getFamily()));
		t.setParent(familyService.toModel(e.getParent()));
		t.setCreatedBy(e.getCreatedBy());
		t.setCreatedOn(e.getCreatedOn());
		return t;
	}

	@Override
	public List<ItemTree> list() {
		List<ItemTreeEntity> l = repository.findByOrderByFamilyAscParentAsc();
		return toModels(l);
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
		return e == null ? null : toModel(e);
	}

	@Override
	public List<ItemTree> listByParent(String parent) {
		List<ItemTreeEntity> trees = repository.findByParentNameOrderByFamilyNameAsc(parent);
		return toModels(trees);
	}

	@Override
	public void save(ItemFamily child, ItemFamily parent) {
		ItemTree t = new ItemTree();
		t.setFamily(child);
		t.setParent(parent);
		save(t);
	}
}