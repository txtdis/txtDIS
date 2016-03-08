package ph.txtdis.controller;

import static org.springframework.http.HttpStatus.OK;
import static org.springframework.web.bind.annotation.RequestMethod.GET;

import java.sql.Date;
import java.time.ZonedDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import static ph.txtdis.util.DateTimeUtils.endOfDay;
import static ph.txtdis.util.DateTimeUtils.startOfDay;

import ph.txtdis.domain.Billing;
import ph.txtdis.dto.Billable;
import ph.txtdis.repository.BillingRepository;
import ph.txtdis.service.BillingToBillableService;

@RestController("deliveryReportController")
@RequestMapping("/deliveryReports")
public class DeliveryReportController extends IdController<BillingRepository, Billing, Long> {

	@Autowired
	private BillingToBillableService fromBilling;

	@RequestMapping(path = "/date", method = GET)
	public ResponseEntity<?> findByDate(@RequestParam("on") Date d) {
		ZonedDateTime start = startOfDay(d.toLocalDate());
		ZonedDateTime end = endOfDay(d.toLocalDate());
		Billing b = repository.findFirstByNumIdNotNullAndNumIdLessThanAndCreatedOnBetweenOrderByCreatedOnAsc(0L, start,
				end);
		Billable i = fromBilling.toBillable(b);
		return new ResponseEntity<>(i, OK);
	}

	@RequestMapping(path = "/first", method = RequestMethod.GET)
	public ResponseEntity<?> first() {
		Billing b = firstSpun();
		Billable i = fromBilling.toBillable(b);
		return new ResponseEntity<>(i, HttpStatus.OK);
	}

	@RequestMapping(path = "/firstToSpin", method = GET)
	public ResponseEntity<?> firstToSpin() {
		Billing b = firstSpun();
		Billable i = spunIdOnlyBillable(b);
		return new ResponseEntity<>(i, OK);
	}

	@RequestMapping(path = "/last", method = RequestMethod.GET)
	public ResponseEntity<?> last() {
		Billing b = lastSpun();
		Billable i = fromBilling.toBillable(b);
		return new ResponseEntity<>(i, HttpStatus.OK);
	}

	@RequestMapping(path = "/lastToSpin", method = GET)
	public ResponseEntity<?> lastToSpin() {
		Billing b = lastSpun();
		Billable i = spunIdOnlyBillable(b);
		return new ResponseEntity<>(i, OK);
	}

	@RequestMapping(path = "/next", method = RequestMethod.GET)
	public ResponseEntity<?> next(@RequestParam("id") Long id) {
		Billing b = repository.findFirstByNumIdNotNullAndNumIdLessThanAndNumIdLessThanOrderByNumIdDesc(0L, id);
		Billable i = fromBilling.toBillable(b);
		return new ResponseEntity<>(i, HttpStatus.OK);
	}

	@RequestMapping(path = "/previous", method = RequestMethod.GET)
	public ResponseEntity<?> previous(@RequestParam("id") Long id) {
		Billing b = repository.findFirstByNumIdNotNullAndNumIdLessThanAndNumIdGreaterThanOrderByNumIdAsc(0L, id);
		Billable i = fromBilling.toBillable(b);
		return new ResponseEntity<>(i, HttpStatus.OK);
	}

	private Billing firstSpun() {
		return repository.findFirstByNumIdNotNullAndNumIdLessThanOrderByNumIdDesc(0L);
	}

	private Billing lastSpun() {
		return repository.findFirstByNumIdNotNullAndNumIdLessThanOrderByNumIdAsc(0L);
	}

	private Billable spunIdOnlyBillable(Billing b) {
		Billable i = new Billable();
		i.setId(b == null ? null : b.getNumId());
		return i;
	}
}