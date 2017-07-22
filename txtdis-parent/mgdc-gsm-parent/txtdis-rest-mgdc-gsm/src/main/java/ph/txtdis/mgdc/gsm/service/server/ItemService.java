package ph.txtdis.mgdc.gsm.service.server;

import java.math.BigDecimal;
import java.util.List;

import ph.txtdis.mgdc.gsm.domain.ItemEntity;
import ph.txtdis.mgdc.gsm.dto.Item;
import ph.txtdis.service.SpunSavedKeyedService;

public interface ItemService //
		extends SpunSavedKeyedService<ItemEntity, Item, Long> {

	void delete(Long id);

	Item findByName(String text);

	Item findByVendorId(String id);

	ItemEntity findEntityByName(String text);

	int getCountPerCase(ItemEntity e);

	BigDecimal getQtyPerCase(ItemEntity e);

	default BigDecimal getQtyPerCase(String name) {
		return getQtyPerCase(findEntityByName(name));
	}

	List<Item> list();

	List<ItemEntity> listEntities();

	List<Item> listFully();

	List<Item> searchByDescription(String text);

	ItemEntity toEntity(Item i);

	Item toModel(ItemEntity e);
}