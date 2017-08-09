package ph.txtdis.mgdc.gsm.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ph.txtdis.dto.Billable;
import ph.txtdis.mgdc.controller.AbstractSpunSavedReferencedKeyedController;
import ph.txtdis.mgdc.gsm.domain.BillableEntity;
import ph.txtdis.mgdc.gsm.service.server.SalesOrderService;

import java.sql.Date;
import java.util.List;

import static org.springframework.http.HttpStatus.OK;
import static org.springframework.web.bind.annotation.RequestMethod.GET;

@RequestMapping("/salesOrders")
@RestController("salesOrderController")
public class SalesOrderController //
	extends AbstractSpunSavedReferencedKeyedController<SalesOrderService, BillableEntity, Billable> {

	@RequestMapping(path = "/unbilled", method = GET)
	public ResponseEntity<?> unbilled(@RequestParam("upTo") Date d) {
		Billable b = service.findUnbilledPickedBooking(d.toLocalDate());
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