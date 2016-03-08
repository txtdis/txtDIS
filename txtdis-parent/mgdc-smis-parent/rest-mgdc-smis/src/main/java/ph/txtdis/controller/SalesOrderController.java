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

@RestController("salesOrderController")
@RequestMapping("/salesOrders")
public class SalesOrderController extends IdController<BillingRepository, Billing, Long> {

	@Autowired
	private CustomerRepository customer;

	@Autowired
	private BillingToBillableService fromBilling;

	@Value("${vendor.id}")
	private String vendorId;

	private Customer vendor;

	@RequestMapping(path = "/date", method = GET)
	public ResponseEntity<?> findByDate(@RequestParam("on") Date d) {
		Billing b = repository
				.findFirstByBookingIdNotNullAndCustomerNotAndRmaNullAndCreatedOnBetweenOrderByCreatedOnAsc(vendor(),
						start(d), end(d));
		Billable a = fromBilling.toBillable(b);
		return new ResponseEntity<>(a, OK);
	}

	@RequestMapping(path = "/first", method = GET)
	public ResponseEntity<?> first() {
		Billing b = firstSpun();
		Billable a = fromBilling.toBillable(b);
		return new ResponseEntity<>(a, OK);
	}

	@RequestMapping(path = "/firstToSpin", method = GET)
	public ResponseEntity<?> firstToSpin() {
		Billing b = firstSpun();
		Billable a = spunIdOnlyBillable(b);
		return new ResponseEntity<>(a, OK);
	}

	@RequestMapping(path = "/last", method = GET)
	public ResponseEntity<?> last() {
		Billing b = lastSpun();
		Billable a = fromBilling.toBillable(b);
		return new ResponseEntity<>(a, OK);
	}

	@RequestMapping(path = "/lastToSpin", method = GET)
	public ResponseEntity<?> lastToSpin() {
		Billing b = lastSpun();
		Billable a = spunIdOnlyBillable(b);
		return new ResponseEntity<>(a, OK);
	}

	@RequestMapping(path = "/next", method = GET)
	public ResponseEntity<?> next(@RequestParam("id") Long id) {
		Billing b = repository
				.findFirstByCustomerNotAndRmaNullAndBookingIdNotNullAndBookingIdGreaterThanOrderByBookingIdAsc(vendor(),
						id);
		Billable a = fromBilling.toBillable(b);
		return new ResponseEntity<>(a, OK);
	}

	@RequestMapping(path = "/previous", method = GET)
	public ResponseEntity<?> previous(@RequestParam("id") Long id) {
		Billing b = repository
				.findFirstByCustomerNotAndRmaNullAndBookingIdNotNullAndBookingIdLessThanOrderByBookingIdDesc(vendor(),
						id);
		Billable a = fromBilling.toBillable(b);
		return new ResponseEntity<>(a, OK);
	}

	private ZonedDateTime end(Date d) {
		return endOfDay(d.toLocalDate());
	}

	private Billing firstSpun() {
		return repository.findFirstByCustomerNotAndRmaNullAndBookingIdNotNullOrderByBookingIdAsc(vendor());
	}

	private Billing lastSpun() {
		return repository.findFirstByCustomerNotAndRmaNullAndBookingIdNotNullOrderByBookingIdDesc(vendor());
	}

	private Billable spunIdOnlyBillable(Billing b) {
		Billable a = new Billable();
		a.setId(b.getBookingId());
		return a;
	}

	private ZonedDateTime start(Date d) {
		return startOfDay(d.toLocalDate());
	}

	private Customer vendor() {
		if (vendor == null)
			vendor = customer.findOne(Long.valueOf(vendorId));
		return vendor;
	}
}