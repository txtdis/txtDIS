package ph.txtdis.mgdc.gsm.service;

import java.util.List;

import ph.txtdis.dto.ItemFamily;
import ph.txtdis.dto.Warehouse;
import ph.txtdis.info.Information;
import ph.txtdis.service.ListedAndResetableService;
import ph.txtdis.service.TitleAndHeaderAndIconAndModuleNamedAndTypeMappedService;
import ph.txtdis.service.UniqueNamedService;

public interface WarehouseService //
		extends ItemFamilyLimited, ListedAndResetableService<Warehouse>, TitleAndHeaderAndIconAndModuleNamedAndTypeMappedService,
		UniqueNamedService<Warehouse> {

	List<String> listNames();

	Warehouse save(String name, ItemFamily family) throws Information, Exception;
}