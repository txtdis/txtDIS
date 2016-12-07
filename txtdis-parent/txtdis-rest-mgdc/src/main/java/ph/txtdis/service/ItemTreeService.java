package ph.txtdis.service;

import java.util.List;

import ph.txtdis.dto.ItemFamily;
import ph.txtdis.dto.ItemTree;

public interface ItemTreeService extends IdService<ItemTree, Long> {

	ItemTree find(ItemFamily family, ItemFamily parent);

	ItemTree findByFamily(ItemFamily family);

	List<ItemTree> listByParent(String parent);

	List<ItemTree> list();

	void save(ItemFamily child, ItemFamily parent);
}