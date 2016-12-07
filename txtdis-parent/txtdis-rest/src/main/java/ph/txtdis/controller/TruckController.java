package ph.txtdis.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import ph.txtdis.dto.Truck;
import ph.txtdis.service.TruckService;

@RestController("truckController")
@RequestMapping("/trucks")
public class TruckController extends AbstractNameListController<TruckService, Truck> {
}