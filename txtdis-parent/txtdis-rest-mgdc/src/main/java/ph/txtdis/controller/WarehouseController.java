package ph.txtdis.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import ph.txtdis.dto.Warehouse;
import ph.txtdis.service.WarehouseService;

@RequestMapping("/warehouses")
@RestController("warehouseController")
public class WarehouseController extends AbstractNameListController<WarehouseService, Warehouse> {
}