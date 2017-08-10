package ph.txtdis.mgdc.ccbpi.service;

import ph.txtdis.dto.Warehouse;
import ph.txtdis.service.ListedAndResettableService;

import java.util.List;

public interface WarehouseService //
	extends ListedAndResettableService<Warehouse> {

	List<String> listNames();
}