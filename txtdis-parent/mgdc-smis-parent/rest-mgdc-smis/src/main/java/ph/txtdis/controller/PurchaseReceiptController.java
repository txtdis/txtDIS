package ph.txtdis.controller;

import static org.springframework.http.HttpStatus.OK;
import static org.springframework.web.bind.annotation.RequestMethod.GET;

import java.sql.Date;
import java.time.ZonedDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import static ph.txtdis.util.DateTimeUtils.endOfDay;
import static ph.txtdis.util.DateTimeUtils.startOfDay;

import ph.txtdis.domain.Billing;
import ph.txtdis.domain.Customer;
import ph.txtdis.dto.Billable;
import ph.txtdis.repository.BillingRepository;
import ph.txtdis.repository.CustomerRepository;
import ph.txtdis.service.BillingToBillableService;

@RestController("purchaseReceiptController")
@RequestMapping("/purchaseReceipts")
public class PurchaseReceiptController extends IdController<BillingRepository, Billing, Long> {

	@Autowired
	private BillingToBillableService fromBilling;

	@Autowired
	private CustomerRepository customer;

	@Value("${vendor.id}")
	private String vendorId;

	private Customer vendor;

	@RequestMapping(path = "/date", method = GET)
	public ResponseEntity<?> findByDate(@RequestParam("on") Date d) {
		ZonedDateTime start = startOfDay(d.toLocalDate());
		ZonedDateTime end = endOfDay(d.toLocalDate());
		Billing b = repository.findFirstByCustomerAndReceivingIdNotNullAndCreatedOnBetweenOrderByCreatedOnAsc(vendor(),
				start, end);
		Billable i = fromBilling.toBillable(b);
		return new ResponseEntity<>(i, OK);
	}

	@RequestMapping(path = "/first", method = GET)
	public ResponseEntity<?> first() {
		Billing b = firstSpun();
		Billable i = fromBilling.toBillable(b);
		return new ResponseEntity<>(i, OK);
	}

	@RequestMapping(path = "/firstToSpin", method = GET)
	public ResponseEntity<?> firstToSpin() {
		Billing b = firstSpun();
		Billable i = spunIdOnlyBillable(b);
		return new ResponseEntity<>(i, OK);
	}

	@RequestMapping(path = "/last", method = GET)
	public ResponseEntity<?> last() {
		Billing b = lastSpun();
		Billable i = fromBilling.toBillable(b);
		return new ResponseEntity<>(i, OK);
	}

	@RequestMapping(path = "/lastToSpin", method = GET)
	public ResponseEntity<?> lastToSpin() {
		Billing b = lastSpun();
		Billable i = spunIdOnlyBillable(b);
		return new ResponseEntity<>(i, OK);
	}

	@RequestMapping(path = "/next", method = GET)
	public ResponseEntity<?> next(@RequestParam("id") Long id) {
		Billing b = repository
				.findFirstByCustomerAndReceivingIdNotNullAndReceivingIdGreaterThanOrderByReceivingIdAsc(vendor(), id);
		Billable i = fromBilling.toBillable(b);
		return new ResponseEntity<>(i, OK);
	}

	@RequestMapping(path = "/previous", method = GET)
	public ResponseEntity<?> previous(@RequestParam("id") Long id) {
		Billing b = repository
				.findFirstByCustomerAndReceivingIdNotNullAndReceivingIdLessThanOrderByReceivingIdDesc(vendor(), id);
		Billable i = fromBilling.toBillable(b);
		return new ResponseEntity<>(i, OK);
	}

	private Billing firstSpun() {
		return repository.findFirstByCustomerAndReceivingIdNotNullOrderByReceivingIdAsc(vendor());
	}

	private Billing lastSpun() {
		return repository.findFirstByCustomerAndReceivingIdNotNullOrderByReceivingIdDesc(vendor());
	}

	private Billable spunIdOnlyBillable(Billing b) {
		Billable i = new Billable();
		i.setId(b.getReceivingId());
		return i;
	}

	private Customer vendor() {
		if (vendor == null)
			vendor = customer.findOne(Long.valueOf(vendorId));
		return vendor;
	}
}