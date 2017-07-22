package ph.txtdis.mgdc.ccbpi.controller;

import static org.springframework.http.HttpStatus.OK;
import static org.springframework.web.bind.annotation.RequestMethod.GET;

import java.sql.Date;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import ph.txtdis.dto.Billable;
import ph.txtdis.mgdc.ccbpi.domain.BillableEntity;
import ph.txtdis.mgdc.ccbpi.service.server.OrderConfirmationService;
import ph.txtdis.mgdc.controller.AbstractSpunSavedReferencedKeyedController;

@RequestMapping("/orderConfirmations")
@RestController("orderConfirmationController")
public class OrderConfirmationController //
		extends AbstractSpunSavedReferencedKeyedController<OrderConfirmationService, BillableEntity, Billable> {

	@RequestMapping(path = "/deliveredValue", method = GET)
	public ResponseEntity<?> deliveredValue( //
			@RequestParam("collector") String collector, //
			@RequestParam("start") Date start, //
			@RequestParam("end") Date end) {
		Billable b = service.getWithDeliveredValue(collector, start.toLocalDate(), end.toLocalDate());
		return new ResponseEntity<>(b, OK);
	}

	@RequestMapping(path = "/ocs", method = GET)
	public ResponseEntity<?> ocs( //
			@RequestParam("date") Date d, //
			@RequestParam("outletId") Long customerVendorId, //
			@RequestParam("count") Long count) {
		Billable b = service.find(d.toLocalDate(), customerVendorId, count);
		return new ResponseEntity<>(b, OK);
	}

	@RequestMapping(path = "/unpicked", method = GET)
	public ResponseEntity<?> unpicked( //
			@RequestParam("date") Date d) {
		List<Billable> l = service.findAllUnpicked(d.toLocalDate());
		return new ResponseEntity<>(l, OK);
	}
}