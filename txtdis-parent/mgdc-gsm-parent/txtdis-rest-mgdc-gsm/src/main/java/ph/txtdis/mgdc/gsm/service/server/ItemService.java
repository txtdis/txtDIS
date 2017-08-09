package ph.txtdis.mgdc.gsm.service.server;

import ph.txtdis.mgdc.gsm.domain.ItemEntity;
import ph.txtdis.mgdc.gsm.dto.Item;
import ph.txtdis.service.SpunSavedKeyedService;

import java.math.BigDecimal;
import java.util.List;

public interface ItemService //
	extends SpunSavedKeyedService<ItemEntity, Item, Long> {

	void delete(Long id);

	Item findByName(String text);

	Item findByVendorId(String id);

	int getCountPerCase(ItemEntity e);

	default BigDecimal getQtyPerCase(String name) {
		return getQtyPerCase(findEntityByName(name));
	}

	BigDecimal getQtyPerCase(ItemEntity e);

	ItemEntity findEntityByName(String text);

	List<Item> list();

	List<ItemEntity> listEntities();

	List<Item> listFully();

	List<Item> searchByDescription(String text);

	ItemEntity toEntity(Item i);

	Item toModel(ItemEntity e);
}