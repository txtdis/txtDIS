package ph.txtdis.mgdc.ccbpi.service;

import java.util.List;

import ph.txtdis.dto.Warehouse;
import ph.txtdis.service.ListedAndResettableService;

public interface WarehouseService //
	extends ListedAndResettableService<Warehouse> {

	List<String> listNames();
}