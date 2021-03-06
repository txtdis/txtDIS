package ph.txtdis.mgdc.ccbpi.service.server;

import java.math.BigDecimal;
import java.util.List;

import ph.txtdis.dto.BillableDetail;
import ph.txtdis.mgdc.ccbpi.domain.ItemEntity;
import ph.txtdis.mgdc.ccbpi.dto.Item;
import ph.txtdis.service.SpunSavedKeyedService;

public interface ItemService
	extends SpunSavedKeyedService<ItemEntity, Item, Long> {

	void delete(Long id);

	Item findByName(String text);

	Item findByVendorId(String id);

	ItemEntity findEntity(BillableDetail billableDetail);

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