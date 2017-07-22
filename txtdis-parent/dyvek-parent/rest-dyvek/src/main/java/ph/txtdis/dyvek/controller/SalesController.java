package ph.txtdis.dyvek.controller;

import static org.springframework.http.HttpStatus.OK;
import static org.springframework.web.bind.annotation.RequestMethod.GET;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import ph.txtdis.dyvek.domain.BillableEntity;
import ph.txtdis.dyvek.model.Billable;
import ph.txtdis.dyvek.service.server.SalesService;

@RequestMapping("/salesOrders")
@RestController("salesController")
public class SalesController //
		extends AbstractOpenListedSearchedSpunSavedController<SalesService, BillableEntity, Billable> {

	@RequestMapping(path = "/sales", method = GET)
	public ResponseEntity<?> sales(@RequestParam("no") String no, @RequestParam("of") String client) {
		Billable b = service.findBySalesNo(client, no);
		return new ResponseEntity<>(b, OK);
	}
}