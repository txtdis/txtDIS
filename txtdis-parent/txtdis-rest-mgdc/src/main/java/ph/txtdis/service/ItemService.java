package ph.txtdis.service;

import java.util.List;

import ph.txtdis.domain.ItemEntity;
import ph.txtdis.dto.BillableDetail;
import ph.txtdis.dto.Item;

public interface ItemService extends SpunService<Item, Long> {

	void delete(Long id);

	Item findByName(String name);

	Item findByVendorId(String id);

	ItemEntity findEntity(BillableDetail billableDetail);

	ItemEntity findEntity(Long itemId);

	int getQtyPerCase(ItemEntity item);

	List<Item> list();

	List<ItemEntity> listEntities();

	List<Item> searchByDescription(String description);

	Item toDTO(ItemEntity itemEntity);

	ItemEntity toEntity(Item item);
}