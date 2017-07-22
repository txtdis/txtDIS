package ph.txtdis.dyvek.controller;

import static org.springframework.http.HttpStatus.OK;
import static org.springframework.web.bind.annotation.RequestMethod.GET;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import ph.txtdis.controller.AbstractSavedKeyedController;
import ph.txtdis.dyvek.domain.CashAdvanceEntity;
import ph.txtdis.dyvek.model.CashAdvance;
import ph.txtdis.dyvek.service.server.CashAdvanceService;
import ph.txtdis.type.PartnerType;

@RequestMapping("/cashAdvances")
@RestController("cashAdvanceController")
public class CashAdvanceController //
		extends AbstractSavedKeyedController<CashAdvanceService, CashAdvanceEntity, CashAdvance, Long> {

	@RequestMapping(path = "/check", method = GET)
	public ResponseEntity<?> check( //
			@RequestParam("bank") String bank, //
			@RequestParam("id") Long id) {
		CashAdvance ca = service.findByCheck(bank, id);
		return new ResponseEntity<>(ca, OK);
	}

	@RequestMapping(method = GET)
	public ResponseEntity<?> list() {
		List<CashAdvance> l = service.findByOrderByBalanceValueDescIssuedDateDesc();
		return new ResponseEntity<>(l, OK);
	}

	@RequestMapping(path = "/customer", method = GET)
	public ResponseEntity<?> referenceNo(@RequestParam("type") PartnerType type, @RequestParam("name") String name) {
		List<CashAdvance> l = service.findByCustomer(type, name);
		return new ResponseEntity<>(l, OK);
	}
}