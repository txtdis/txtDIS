package ph.txtdis.controller;

import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

import java.sql.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import ph.txtdis.dto.Billable;
import ph.txtdis.dto.Remittance;
import ph.txtdis.exception.DateBeforeGoLiveException;
import ph.txtdis.exception.EndDateBeforeStartException;
import ph.txtdis.service.RemittanceService;
import ph.txtdis.type.PaymentType;

@RequestMapping("/remittances")
@RestController("remittanceController")
public class RemittanceController extends AbstractSpunController<RemittanceService, Remittance, Long> {

	@Value("${go.live}")
	private String goLive;

	@RequestMapping(path = "/find", method = GET)
	public ResponseEntity<?> findByBilling(@RequestParam("billing") Billable b) {
		List<Remittance> p = service.listRemittanceByBilling(b);
		return new ResponseEntity<>(p, OK);
	}

	@RequestMapping(path = "/check", method = GET)
	public ResponseEntity<?> findByCheck(@RequestParam("bank") Long bankId, @RequestParam("id") Long id) {
		Remittance p = service.findByCheck(bankId, id);
		return new ResponseEntity<>(p, OK);
	}

	@RequestMapping(path = "/collector", method = GET)
	public ResponseEntity<?> findByCollector(@RequestParam("name") String n, @RequestParam("date") Date d) {
		Remittance p = service.findByCollector(n, d.toLocalDate());
		return new ResponseEntity<>(p, OK);
	}

	@RequestMapping(path = "/date", method = GET)
	public ResponseEntity<?> findByDate(@RequestParam("on") Date d) {
		Remittance p = service.findByDate(d.toLocalDate());
		return new ResponseEntity<>(p, OK);
	}

	@RequestMapping(path = "/undeposited", method = GET)
	public ResponseEntity<?> findByUndepositedPayments(@RequestParam("payType") PaymentType t,
			@RequestParam("seller") String s, @RequestParam("upTo") Date d) {
		Remittance p = service.findByUndepositedPayments(t, s, d.toLocalDate());
		return new ResponseEntity<>(p, OK);
	}

	@RequestMapping(path = "/invalidated", method = GET)
	public ResponseEntity<?> findIfCurrentlyInvalidated(@RequestParam("id") Long id) {
		Remittance p = service.findIfCurrentlyInvalidated(id);
		return new ResponseEntity<>(p, OK);
	}

	@RequestMapping(path = "/list", method = GET)
	public ResponseEntity<?> list(@RequestParam("start") Date s, @RequestParam("end") Date e)
			throws DateBeforeGoLiveException, EndDateBeforeStartException {
		List<Remittance> l = service.list(s.toLocalDate(), e.toLocalDate());
		return new ResponseEntity<>(l, OK);
	}

	@RequestMapping(method = GET)
	public ResponseEntity<?> list() {
		List<Remittance> p = service.list();
		return new ResponseEntity<>(p, OK);
	}

	@RequestMapping(path = "/all", method = POST)
	public ResponseEntity<?> save(@RequestBody List<Remittance> p) {
		p = service.save(p);
		return new ResponseEntity<>(p, CREATED);
	}
}