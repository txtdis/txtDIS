package ph.txtdis.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import ph.txtdis.dto.Truck;
import ph.txtdis.service.ServerTruckService;

@RestController("truckController")
@RequestMapping("/trucks")
public class TruckController extends AbstractNameListController<ServerTruckService, Truck> {
}