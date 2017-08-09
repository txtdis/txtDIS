package ph.txtdis.mgdc.gsm.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ph.txtdis.dto.Billable;
import ph.txtdis.mgdc.controller.AbstractSpunSavedReferencedKeyedController;
import ph.txtdis.mgdc.gsm.domain.BillableEntity;
import ph.txtdis.mgdc.gsm.service.server.AllBillingService;
import ph.txtdis.type.ModuleType;

import java.sql.Date;
import java.util.List;

import static org.springframework.http.HttpStatus.OK;
import static org.springframework.web.bind.annotation.RequestMethod.GET;

@RequestMapping("/billables")
@RestController("billableController")
public class BillableController //
	extends AbstractSpunSavedReferencedKeyedController<AllBillingService, BillableEntity, Billable> {

	@RequestMapping(path = "/aged", method = GET)
	public ResponseEntity<?> aged(@RequestParam("customer") Long id) {
		List<Billable> l = service.findAllAged(id);
		return new ResponseEntity<>(l, OK);
	}

	@RequestMapping(path = "/aging", method = GET)
	public ResponseEntity<?> aging(@RequestParam("customer") Long id) {
		List<Billable> l = service.findAllAging(id);
		return new ResponseEntity<>(l, OK);
	}

	@RequestMapping(path = "/billed", method = GET)
	public ResponseEntity<?> billed(@RequestParam("customer") Long id) {
		Billable b = service.findByCustomerId(id);
		return new ResponseEntity<>(b, OK);
	}

	@RequestMapping(path = "/booking", method = GET)
	public ResponseEntity<?> booking(@RequestParam("id") Long id) throws Exception {
		Billable b = service.findByBookingId(id);
		return new ResponseEntity<>(b, OK);
	}

	@RequestMapping(path = "/latest", method = GET)
	public ResponseEntity<?> latest(@RequestParam("prefix") String prefix, @RequestParam("suffix") String suffix,
	                                @RequestParam("startId") Long startId, @RequestParam("endId") Long endId) {
		Billable b = service.findLatest(prefix, suffix, startId, endId);
		return new ResponseEntity<>(b, OK);
	}

	@RequestMapping(path = "/notFullyPaidCOD", method = GET)
	public ResponseEntity<?> notFullyPaidCOD(@RequestParam("upTo") Date d) {
		Billable b = service.findNotFullyPaidNotInvalidCOD(d.toLocalDate());
		return new ResponseEntity<>(b, OK);
	}

	@RequestMapping(path = "/orderNo", method = GET)
	public ResponseEntity<?> orderNo(@RequestParam("prefix") String p,
	                                 @RequestParam("id") Long id,
	                                 @RequestParam("suffix") String s) {
		Billable b = service.findByOrderNo(p, id, s);
		return new ResponseEntity<>(b, OK);
	}

	@RequestMapping(path = "/purchased", method = GET)
	public ResponseEntity<?> purchased(@RequestParam("by") Long customerId, @RequestParam("item") Long itemId) {
		Billable b = service.findByCustomerPurchasedItemId(customerId, itemId);
		return new ResponseEntity<>(b, OK);
	}

	@RequestMapping(path = "/uncorrected", method = GET)
	public ResponseEntity<?> uncorrected(@RequestParam("type") ModuleType t) {
		Billable b = service.findUncorrected(t);
		return new ResponseEntity<>(b, OK);
	}

	@RequestMapping(path = "/unvalidatedCorrected", method = GET)
	public ResponseEntity<?> unvalidatedCorrected(@RequestParam("type") ModuleType t) {
		Billable b = service.findUnvalidatedCorrected(t);
		return new ResponseEntity<>(b, OK);
	}
}