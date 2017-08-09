package ph.txtdis.mgdc.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ph.txtdis.controller.AbstractNameListController;
import ph.txtdis.dto.Warehouse;
import ph.txtdis.mgdc.service.server.WarehouseService;

@RequestMapping("/warehouses")
@RestController("warehouseController")
public class WarehouseController //
	extends AbstractNameListController<WarehouseService, Warehouse> {
}