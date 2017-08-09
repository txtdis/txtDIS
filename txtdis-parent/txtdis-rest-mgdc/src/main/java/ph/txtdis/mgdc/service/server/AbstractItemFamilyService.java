package ph.txtdis.mgdc.service.server;

import ph.txtdis.dto.ItemFamily;
import ph.txtdis.mgdc.domain.ItemFamilyEntity;
import ph.txtdis.mgdc.repository.ItemFamilyRepository;
import ph.txtdis.service.AbstractCreateNameListService;

public abstract class AbstractItemFamilyService //
	extends AbstractCreateNameListService<ItemFamilyRepository, ItemFamilyEntity, ItemFamily> //
	implements ConvertibleItemFamilyService {

	@Override
	public ItemFamily toModel(ItemFamilyEntity e) {
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
}