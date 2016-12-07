package ph.txtdis.controller;

import static org.springframework.http.HttpStatus.OK;
import static org.springframework.web.bind.annotation.RequestMethod.GET;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import ph.txtdis.dto.Billable;
import ph.txtdis.service.DeliveryListService;

@RequestMapping("/deliveryLists")
@RestController("deliveryListController")
public class DeliveryListController extends AbstractBillableController<DeliveryListService> {

	@RequestMapping(path = "/deliveryList", method = GET)
	public ResponseEntity<?> findDeliveryList(@RequestParam("shipment") Long id, @RequestParam("route") String r) {
		Billable i = service.find(id, r);
		return new ResponseEntity<>(i, OK);
	}
}