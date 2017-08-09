package ph.txtdis.mgdc.ccbpi.controller;

import static org.springframework.http.HttpStatus.OK;
import static org.springframework.web.bind.annotation.RequestMethod.GET;

import java.sql.Date;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import ph.txtdis.dto.Billable;
import ph.txtdis.exception.NotFoundException;
import ph.txtdis.mgdc.ccbpi.domain.BillableEntity;
import ph.txtdis.mgdc.ccbpi.service.server.OrderReturnService;
import ph.txtdis.mgdc.controller.AbstractSpunSavedReferencedKeyedController;

@RequestMapping("/orderReturns")
@RestController("orderReturnController")
public class OrderReturnController //
	extends AbstractSpunSavedReferencedKeyedController<OrderReturnService, BillableEntity, Billable> {

	@RequestMapping(path = "/orderReturn", method = GET)
	public ResponseEntity<?> orderReturn(@RequestParam("id") Long id) throws NotFoundException {
		Billable b = service.findByPrimaryKey(id);
		return new ResponseEntity<>(b, OK);
	}

	@RequestMapping(path = "/rr", method = GET)
	public ResponseEntity<?> rr( //
	                             @RequestParam("date") Date d, //
	                             @RequestParam("outletId") Long customerVendorId, //
	                             @RequestParam("count") Long count) {
		Billable i = service.find(d.toLocalDate(), customerVendorId, count);
		return new ResponseEntity<>(i, OK);
	}
}