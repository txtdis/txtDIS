package ph.txtdis.service;

import java.util.List;

import ph.txtdis.dto.Item;

public interface EmptiesItemService extends ItemService {

	List<Item> findEmpties();
}
