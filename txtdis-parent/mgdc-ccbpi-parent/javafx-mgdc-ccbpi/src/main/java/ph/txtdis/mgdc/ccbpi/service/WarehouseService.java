package ph.txtdis.mgdc.ccbpi.service;

import java.util.List;

import ph.txtdis.dto.Warehouse;
import ph.txtdis.service.ListedAndResetableService;

public interface WarehouseService //
		extends ListedAndResetableService<Warehouse> {

	List<String> listNames();
}