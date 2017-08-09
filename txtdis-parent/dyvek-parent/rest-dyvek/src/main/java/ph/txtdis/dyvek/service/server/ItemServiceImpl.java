package ph.txtdis.dyvek.service.server;

import org.springframework.stereotype.Service;
import ph.txtdis.dyvek.domain.ItemEntity;
import ph.txtdis.dyvek.model.Item;
import ph.txtdis.dyvek.repository.ItemRepository;
import ph.txtdis.service.AbstractCreateNameListService;

@Service("itemService")
public class ItemServiceImpl //
	extends AbstractCreateNameListService<ItemRepository, ItemEntity, Item> //
	implements ItemService {

	@Override
	public ItemEntity findEntityByName(String name) {
		return super.findEntityByName(name);
	}

	@Override
	protected Item toModel(ItemEntity e) {
		if (e == null)
			return null;
		Item c = new Item();
		c.setId(e.getId());
		c.setName(e.getName());
		c.setCreatedBy(e.getCreatedBy());
		c.setCreatedOn(e.getCreatedOn());
		return c;
	}

	@Override
	protected ItemEntity toEntity(Item c) {
		if (c == null)
			return null;
		ItemEntity e = new ItemEntity();
		e.setName(c.getName());
		return e;
	}
}