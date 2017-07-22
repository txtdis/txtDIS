package ph.txtdis.mgdc.gsm.controller;

import static org.springframework.http.HttpStatus.OK;
import static org.springframework.web.bind.annotation.RequestMethod.GET;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import ph.txtdis.dto.Billable;
import ph.txtdis.mgdc.controller.AbstractSpunSavedReferencedKeyedController;
import ph.txtdis.mgdc.gsm.domain.BillableEntity;
import ph.txtdis.mgdc.gsm.service.server.GoodRmaService;

@RequestMapping("/goodRmas")
@RestController("goodRmaController")
public class GoodRmaController //
		extends AbstractSpunSavedReferencedKeyedController<GoodRmaService, BillableEntity, Billable> {

	@RequestMapping(path = "/pendingReturn", method = GET)
	public ResponseEntity<?> pendingReturn(@RequestParam("customer") Long id) throws Exception {
		Billable b = service.findOpenRmaByCustomerId(id);
		return new ResponseEntity<>(b, OK);
	}

	@RequestMapping(path = "/print", method = GET)
	public ResponseEntity<?> print(@RequestParam("id") Long id) throws Exception {
		Billable b = service.print(id);
		return new ResponseEntity<>(b, OK);
	}
}