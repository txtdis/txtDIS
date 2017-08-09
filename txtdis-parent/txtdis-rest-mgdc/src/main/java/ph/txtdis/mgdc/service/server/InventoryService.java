package ph.txtdis.mgdc.service.server;

import ph.txtdis.dto.Inventory;
import ph.txtdis.dto.Stock;

import java.util.List;

public interface InventoryService {

	List<Inventory> list();

	Inventory next(Long id);

	List<Stock> update();
}