package ph.txtdis.service;

import java.util.List;

import ph.txtdis.dto.ItemFamily;
import ph.txtdis.dto.ItemTree;
import ph.txtdis.info.SuccessfulSaveInfo;

public interface ItemTreeService extends Listed<ItemTree>, SavedByEntity<ItemTree>, Titled {

	boolean isDuplicated(ItemFamily family, ItemFamily parent) throws Exception;

	List<ItemFamily> listFamilies() throws Exception;

	List<ItemFamily> listParents(ItemFamily family) throws Exception;

	ItemTree save(ItemFamily family, ItemFamily parent) throws SuccessfulSaveInfo, Exception;
}