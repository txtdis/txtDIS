package ph.txtdis.mgdc.gsm.service;

import ph.txtdis.dto.ItemFamily;
import ph.txtdis.dto.ItemTree;
import ph.txtdis.info.Information;
import ph.txtdis.service.ListedAndResettableService;
import ph.txtdis.service.SavedByEntity;
import ph.txtdis.service.TitleAndHeaderAndIconAndModuleNamedAndTypeMappedService;

import java.util.List;

public interface ItemTreeService
	extends ListedAndResettableService<ItemTree>,
	SavedByEntity<ItemTree>,
	TitleAndHeaderAndIconAndModuleNamedAndTypeMappedService {

	boolean isDuplicated(ItemFamily family, ItemFamily parent) throws Exception;

	List<ItemFamily> listFamilies() throws Exception;

	List<ItemFamily> listParents(ItemFamily family) throws Exception;

	ItemTree save(ItemFamily family, ItemFamily parent) throws Information, Exception;
}