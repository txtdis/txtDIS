package ph.txtdis.mgdc.service.server;

import java.util.List;

import ph.txtdis.dto.Inventory;
import ph.txtdis.dto.Stock;

public interface InventoryService {

	List<Inventory> list();

	Inventory next(Long id);

	List<Stock> update();
}