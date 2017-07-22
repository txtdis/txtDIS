package ph.txtdis.mgdc.gsm.controller;

import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

import java.sql.Date;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import ph.txtdis.controller.AbstractSpunSavedKeyedController;
import ph.txtdis.dto.Billable;
import ph.txtdis.dto.Remittance;
import ph.txtdis.exception.NotFoundException;
import ph.txtdis.mgdc.gsm.domain.RemittanceEntity;
import ph.txtdis.mgdc.gsm.service.server.GsmRemittanceService;
import ph.txtdis.type.PaymentType;

@RequestMapping("/remittances")
@RestController("remittanceController")
public class RemittanceController //
		extends AbstractSpunSavedKeyedController<GsmRemittanceService, RemittanceEntity, Remittance> {

	@RequestMapping(path = "/all", method = POST)
	public ResponseEntity<?> all(@RequestBody List<Remittance> l) {
		l = service.save(l);
		return new ResponseEntity<>(l, CREATED);
	}

	@RequestMapping(path = "/billing", method = GET)
	public ResponseEntity<?> billing( //
			@RequestParam("id") Long id) {
		Remittance r = service.findByBillingId(id);
		return new ResponseEntity<>(r, OK);
	}

	@RequestMapping(path = "/billings", method = GET)
	public ResponseEntity<?> billings( //
			@RequestParam("id") Billable b) {
		List<Remittance> r = service.findAll(b);
		return new ResponseEntity<>(r, OK);
	}

	@RequestMapping(path = "/check", method = GET)
	public ResponseEntity<?> check( //
			@RequestParam("bank") String bank, //
			@RequestParam("id") Long id) {
		Remittance r = service.findByCheck(bank, id);
		return new ResponseEntity<>(r, OK);
	}

	@RequestMapping(path = "/checks", method = GET)
	public ResponseEntity<?> checks(//
			@RequestParam("bank") Long bankId) {
		List<Remittance> l = service.findAllUnvalidatedChecksByBank(bankId);
		return new ResponseEntity<>(l, OK);
	}

	@RequestMapping(path = "/collector", method = GET)
	public ResponseEntity<?> collector( //
			@RequestParam("name") String n, //
			@RequestParam("date") Date d) {
		Remittance r = service.findByCollector(n, d.toLocalDate());
		return new ResponseEntity<>(r, OK);
	}

	@RequestMapping(path = "/date", method = GET)
	public ResponseEntity<?> date( //
			@RequestParam("on") Date d) {
		Remittance r = service.findByDate(d.toLocalDate());
		return new ResponseEntity<>(r, OK);
	}

	@RequestMapping(path = "/find", method = GET)
	public ResponseEntity<?> find( //
			@RequestParam("id") Long id) throws NotFoundException {
		Remittance r = service.findByPrimaryKey(id);
		return new ResponseEntity<>(r, OK);
	}

	@RequestMapping(path = "/invalid", method = GET)
	public ResponseEntity<?> invalid( //
			@RequestParam("id") Long id) {
		Remittance r = service.findInvalidByBillingId(id);
		return new ResponseEntity<>(r, OK);
	}

	@RequestMapping(method = GET)
	public ResponseEntity<?> list() {
		List<Remittance> l = service.findAllUnvalidated();
		return new ResponseEntity<>(l, OK);
	}

	@RequestMapping(path = "/list", method = GET)
	public ResponseEntity<?> list( //
			@RequestParam("start") Date s, //
			@RequestParam("end") Date e) throws Exception {
		List<Remittance> l = service.findAll(s.toLocalDate(), e.toLocalDate());
		return new ResponseEntity<>(l, OK);
	}

	@RequestMapping(path = "/listPerCollector", method = GET)
	public ResponseEntity<?> listPerCollector( //
			@RequestParam("name") String collector, //
			@RequestParam("start") Date s, //
			@RequestParam("end") Date e) throws Exception {
		List<Remittance> l = service.findAll(collector, s.toLocalDate(), e.toLocalDate());
		return new ResponseEntity<>(l, OK);
	}

	@RequestMapping(path = "/pending", method = GET)
	public ResponseEntity<?> pending() {
		Remittance r = service.findPending();
		return new ResponseEntity<>(r, OK);
	}

	@RequestMapping(path = "/undeposited", method = GET)
	public ResponseEntity<?> undeposited( //
			@RequestParam("payType") PaymentType t, //
			@RequestParam("seller") String s, //
			@RequestParam("upTo") Date d) {
		Remittance r = service.findByUndepositedPayments(t, s, d.toLocalDate());
		return new ResponseEntity<>(r, OK);
	}
}