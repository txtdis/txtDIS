package ph.txtdis.service;

import java.util.List;

import org.springframework.stereotype.Service;

import ph.txtdis.domain.ItemEntity;
import ph.txtdis.dto.Item;

@Service("itemService")
public class ItemServiceImpl extends AbstractItemService implements EmptiesItemService {

	@Override
	public List<Item> findEmpties() {
		List<ItemEntity> l = repository.findByNameStartingWith("CASE");
		return toIdAndNameOnlyList(l);
	}
}