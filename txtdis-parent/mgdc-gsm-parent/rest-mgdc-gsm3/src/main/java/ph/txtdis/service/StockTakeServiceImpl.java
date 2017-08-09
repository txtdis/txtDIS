package ph.txtdis.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ph.txtdis.dto.StockTake;
import ph.txtdis.mgdc.gsm.service.server.AbstractStockTakeService;
import ph.txtdis.mgdc.gsm.service.server.InventoryRestClientService;

@Service("stockTakeService")
public class StockTakeServiceImpl //
	extends AbstractStockTakeService {

	@Autowired
	private InventoryRestClientService<StockTake> inventoryRestClientService;

	@Override
	public StockTake last() {
		try {
			return inventoryRestClientService.module("stockTake").getOne("/last");
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
}