package ph.txtdis.mgdc.gsm.service;

import java.util.List;

import ph.txtdis.dto.ItemFamily;
import ph.txtdis.dto.ItemTree;
import ph.txtdis.info.Information;
import ph.txtdis.service.ListedAndResetableService;
import ph.txtdis.service.SavedByEntity;
import ph.txtdis.service.TitleAndHeaderAndIconAndModuleNamedAndTypeMappedService;

public interface ItemTreeService extends ListedAndResetableService<ItemTree>, SavedByEntity<ItemTree>, TitleAndHeaderAndIconAndModuleNamedAndTypeMappedService {

	boolean isDuplicated(ItemFamily family, ItemFamily parent) throws Exception;

	List<ItemFamily> listFamilies() throws Exception;

	List<ItemFamily> listParents(ItemFamily family) throws Exception;

	ItemTree save(ItemFamily family, ItemFamily parent) throws Information, Exception;
}