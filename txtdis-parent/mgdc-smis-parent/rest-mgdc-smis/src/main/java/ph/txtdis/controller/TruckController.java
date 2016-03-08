package ph.txtdis.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import ph.txtdis.domain.Truck;
import ph.txtdis.repository.TruckRepository;

@RestController("truckController")
@RequestMapping("/trucks")
public class TruckController extends NameListController<TruckRepository, Truck> {
}