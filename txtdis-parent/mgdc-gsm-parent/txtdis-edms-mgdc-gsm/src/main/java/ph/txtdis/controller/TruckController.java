package ph.txtdis.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ph.txtdis.dto.Truck;
import ph.txtdis.service.EdmsTruckService;

@RequestMapping("/trucks")
@RestController("truckController")
public class TruckController //
	extends AbstractNameListController<EdmsTruckService, Truck> {
}