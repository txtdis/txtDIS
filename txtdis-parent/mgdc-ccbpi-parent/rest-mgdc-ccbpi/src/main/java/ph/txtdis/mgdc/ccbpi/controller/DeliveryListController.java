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
import ph.txtdis.mgdc.ccbpi.service.server.DeliveryListService;
import ph.txtdis.mgdc.controller.AbstractSpunSavedReferencedKeyedController;

@RequestMapping("/deliveryLists")
@RestController("deliveryListController")
public class DeliveryListController //
		extends AbstractSpunSavedReferencedKeyedController<DeliveryListService, BillableEntity, Billable> {

	@RequestMapping(path = "/deliveryList", method = GET)
	public ResponseEntity<?> deliveryList(@RequestParam("id") Long id) throws NotFoundException {
		Billable b = service.findByPrimaryKey(id);
		return new ResponseEntity<>(b, OK);
	}

	@RequestMapping(path = "/ddl", method = GET)
	public ResponseEntity<?> ddl(@RequestParam("date") Date date, @RequestParam("route") String r) {
		Billable i = service.find(date.toLocalDate(), r);
		return new ResponseEntity<>(i, OK);
	}
}