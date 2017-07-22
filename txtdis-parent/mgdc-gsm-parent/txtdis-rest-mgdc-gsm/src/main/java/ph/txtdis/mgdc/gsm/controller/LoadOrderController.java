package ph.txtdis.mgdc.gsm.controller;

import static org.springframework.http.HttpStatus.OK;
import static org.springframework.web.bind.annotation.RequestMethod.GET;

import java.sql.Date;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import ph.txtdis.dto.Billable;
import ph.txtdis.mgdc.controller.AbstractSpunSavedReferencedKeyedController;
import ph.txtdis.mgdc.gsm.domain.BillableEntity;
import ph.txtdis.mgdc.gsm.service.server.LoadOrderService;

@RequestMapping("/loadOrders")
@RestController("loadOrderController")
public class LoadOrderController //
		extends AbstractSpunSavedReferencedKeyedController<LoadOrderService, BillableEntity, Billable> {

	@RequestMapping(path = "/bookedLoadOrder", method = GET)
	public ResponseEntity<?> bookedLoadOrder(@RequestParam("date") Date d, @RequestParam("exTruck") String t) {
		Billable b = service.find(d.toLocalDate(), t);
		return new ResponseEntity<>(b, OK);
	}

	@RequestMapping(path = "/booked", method = GET)
	public ResponseEntity<?> bookedonAction(@RequestParam("on") Date d) {
		List<Billable> l = service.findBooked(d.toLocalDate());
		return new ResponseEntity<>(l, OK);
	}

	@RequestMapping(path = "/loadVariance", method = GET)
	public ResponseEntity<?> loadVariance(@RequestParam("upTo") Date d) {
		Billable b = service.findWithLoadVariance(d.toLocalDate());
		return new ResponseEntity<>(b, OK);
	}

	@RequestMapping(path = "/shortage", method = GET)
	public ResponseEntity<?> shortage(@RequestParam("id") Long id) {
		Billable b = service.findShort(id);
		return new ResponseEntity<>(b, OK);
	}

	@RequestMapping(path = "/forPicking", method = GET)
	public ResponseEntity<?> forPicking(@RequestParam("on") Date d) {
		List<Billable> l = service.findAllUnpicked(d.toLocalDate());
		return new ResponseEntity<>(l, OK);
	}

	@RequestMapping(path = "/unpicked", method = GET)
	public ResponseEntity<?> unpicked(@RequestParam("upTo") Date d) {
		Billable b = service.findUnpicked(d.toLocalDate());
		return new ResponseEntity<>(b, OK);
	}
}