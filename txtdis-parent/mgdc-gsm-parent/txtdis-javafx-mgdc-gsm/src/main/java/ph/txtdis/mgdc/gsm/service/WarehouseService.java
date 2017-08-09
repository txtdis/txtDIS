package ph.txtdis.mgdc.gsm.service;

import ph.txtdis.dto.ItemFamily;
import ph.txtdis.dto.Warehouse;
import ph.txtdis.info.Information;
import ph.txtdis.service.ListedAndResettableService;
import ph.txtdis.service.TitleAndHeaderAndIconAndModuleNamedAndTypeMappedService;
import ph.txtdis.service.UniqueNamedService;

import java.util.List;

public interface WarehouseService //
	extends ItemFamilyLimited,
	ListedAndResettableService<Warehouse>,
	TitleAndHeaderAndIconAndModuleNamedAndTypeMappedService,
	UniqueNamedService<Warehouse> {

	List<String> listNames();

	Warehouse save(String name, ItemFamily family) throws Information, Exception;
}