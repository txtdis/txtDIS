package ph.txtdis.mgdc.gsm.service.server;

import java.util.List;

import ph.txtdis.dto.ItemFamily;
import ph.txtdis.dto.ItemTree;
import ph.txtdis.mgdc.gsm.domain.ItemTreeEntity;
import ph.txtdis.service.SavedKeyedService;

public interface ItemTreeService //
		extends SavedKeyedService<ItemTreeEntity, ItemTree, Long> {

	ItemTree find(ItemFamily family, ItemFamily parent);

	ItemTree findByFamily(ItemFamily family);

	List<ItemTree> listByParent(String parent);

	List<ItemTree> list();

	void save(ItemFamily child, ItemFamily parent);
}