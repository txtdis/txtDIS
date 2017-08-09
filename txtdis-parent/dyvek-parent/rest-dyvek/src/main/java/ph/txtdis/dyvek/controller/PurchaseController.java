package ph.txtdis.dyvek.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ph.txtdis.dyvek.domain.BillableEntity;
import ph.txtdis.dyvek.model.Billable;
import ph.txtdis.dyvek.service.server.PurchaseService;

import java.util.List;

import static org.springframework.http.HttpStatus.OK;
import static org.springframework.web.bind.annotation.RequestMethod.GET;

@RequestMapping("/purchaseOrders")
@RestController("purchaseController")
public class PurchaseController //
	extends AbstractOpenListedSearchedSpunSavedController<PurchaseService, BillableEntity, Billable> {

	@RequestMapping(path = "/expiringOrExpiredInTheNext2Days", method = GET)
	public ResponseEntity<?> expiringOrExpired() {
		List<Billable> l = service.findAllOpenEndingInTheNext2Days();
		return new ResponseEntity<>(l, OK);
	}

	@RequestMapping(path = "/purchase", method = GET)
	public ResponseEntity<?> order(@RequestParam("no") String po) {
		Billable i = service.findByPurchaseNo(po);
		return new ResponseEntity<>(i, OK);
	}
}