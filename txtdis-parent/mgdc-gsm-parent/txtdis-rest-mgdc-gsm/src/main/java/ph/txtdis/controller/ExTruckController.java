package ph.txtdis.controller;

import static org.springframework.http.HttpStatus.OK;
import static org.springframework.web.bind.annotation.RequestMethod.GET;

import java.sql.Date;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import ph.txtdis.dto.Billable;
import ph.txtdis.service.ExTruckService;

@RequestMapping("/loadOrders")
@RestController("exTruckController")
public class ExTruckController extends AbstractBillableController<ExTruckService> {

	@RequestMapping(path = "/bookedExTrucks", method = GET)
	public ResponseEntity<?> findBookedExTrucks(@RequestParam("on") Date d) {
		List<Billable> l = service.findBookedExTrucks(d);
		return new ResponseEntity<>(l, OK);
	}

	@RequestMapping(path = "/bookedLoadOrder", method = GET)
	public ResponseEntity<?> findLoadOrder(@RequestParam("date") Date d, @RequestParam("exTruck") String exTruck) {
		Billable b = service.findLoadOrder(d.toLocalDate(), exTruck);
		return new ResponseEntity<>(b, OK);
	}

	@RequestMapping(path = "/openLoadOrder", method = GET)
	public ResponseEntity<?> findNoReceivingReportedLoadOrderUpTo(@RequestParam("seller") String s,
			@RequestParam("upTo") Date d) {
		Billable b = service.findOpenLoadOrder(s, d);
		return new ResponseEntity<>(b, OK);
	}
}