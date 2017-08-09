package ph.txtdis.dyvek.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ph.txtdis.dyvek.domain.BillableEntity;
import ph.txtdis.dyvek.model.Billable;
import ph.txtdis.dyvek.service.server.DeliveryService;

import static org.springframework.http.HttpStatus.OK;
import static org.springframework.web.bind.annotation.RequestMethod.GET;

@RequestMapping("/deliveryReports")
@RestController("deliveryController")
public class DeliveryController //
	extends AbstractSearchedSpunSavedOrderController<DeliveryService, BillableEntity, Billable> {

	@RequestMapping(path = "/delivery", method = GET)
	public ResponseEntity<?> delivery(@RequestParam("no") String dr, @RequestParam("of") String vendor) {
		Billable b = service.findByVendorDeliveryNo(vendor, dr);
		return new ResponseEntity<>(b, OK);
	}
}