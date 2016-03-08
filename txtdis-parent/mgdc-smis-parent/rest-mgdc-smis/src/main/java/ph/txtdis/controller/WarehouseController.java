package ph.txtdis.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import ph.txtdis.domain.Warehouse;
import ph.txtdis.repository.WarehouseRepository;

@RestController("warehouseController")
@RequestMapping("/warehouses")
public class WarehouseController extends NameListController<WarehouseRepository, Warehouse> {
}