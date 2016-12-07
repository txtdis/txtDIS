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
import ph.txtdis.service.OrderConfirmationService;

@RequestMapping("/orderConfirmations")
@RestController("orderConfirmationController")
public class OrderConfirmationController extends AbstractBillableController<OrderConfirmationService> {

	@RequestMapping(path = "/booking", method = GET)
	public ResponseEntity<?> findByBookingId(@RequestParam("id") Long id) {
		Billable i = service.findByBookingId(id);
		return new ResponseEntity<>(i, OK);
	}

	@RequestMapping(path = "/orderConfirmation", method = GET)
	public ResponseEntity<?> findOrderConfirmation(@RequestParam("outletId") Long customerVendorId,
			@RequestParam("date") Date d, @RequestParam("orderNo") Long orderNo) {
		Billable i = service.find(customerVendorId, d.toLocalDate(), orderNo);
		return new ResponseEntity<>(i, OK);
	}

	@RequestMapping(path = "/unpicked", method = GET)
	public ResponseEntity<?> findUnpickedOrderConfirmation(@RequestParam("date") Date d) {
		List<Billable> l = service.findUnpickedOn(d.toLocalDate());
		return new ResponseEntity<>(l, OK);
	}
}