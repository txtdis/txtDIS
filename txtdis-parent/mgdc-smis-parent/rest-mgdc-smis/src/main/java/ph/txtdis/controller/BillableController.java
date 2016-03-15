package ph.txtdis.controller;

import static java.math.BigDecimal.ZERO;
import static java.time.DayOfWeek.SATURDAY;
import static java.time.LocalDate.now;
import static java.time.temporal.ChronoUnit.DAYS;
import static java.util.stream.Collectors.toList;
import static org.apache.log4j.Logger.getLogger;
import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;
import static org.springframework.web.servlet.support.ServletUriComponentsBuilder.fromCurrentContextPath;
import static ph.txtdis.util.DateTimeUtils.endOfDay;
import static ph.txtdis.util.DateTimeUtils.startOfDay;
import static ph.txtdis.util.DateTimeUtils.toDate;

import java.math.BigDecimal;
import java.net.URI;
import java.sql.Date;
import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import ph.txtdis.domain.Billing;
import ph.txtdis.domain.Customer;
import ph.txtdis.domain.Item;
import ph.txtdis.dto.Billable;
import ph.txtdis.exception.FailedPrintingException;
import ph.txtdis.printer.ReturnedMaterialPrinter;
import ph.txtdis.repository.BillingRepository;
import ph.txtdis.repository.CustomerRepository;
import ph.txtdis.service.BillableToBillingService;
import ph.txtdis.service.BillingToBillableService;

@RequestMapping("/billables")
@RestController("billableController")
public class BillableController {

	private static Logger logger = getLogger(BillableController.class);

	@Autowired
	private BillingToBillableService fromBilling;

	@Autowired
	private BillableToBillingService fromBillable;

	@Autowired
	private BillingRepository repository;

	@Autowired
	private CustomerRepository customerRepo;

	@Autowired
	private ReturnedMaterialPrinter returnedMaterialPrinter;

	@Value("${go.live}")
	private String goLive;

	@Value("${grace.period.invoicing}")
	private String invoicingGracePeriod;

	@Value("${vendor.id}")
	private String vendorId;

	private Customer vendor;

	@RequestMapping(path = "/{id}", method = GET)
	public ResponseEntity<?> find(@PathVariable Long id) {
		Billing b = repository.findOne(id);
		Billable i = fromBilling.toBillable(b);
		return new ResponseEntity<>(i, OK);
	}

	@RequestMapping(path = "/badOrders", method = GET)
	public ResponseEntity<?> findBadOrderByCustomer(@RequestParam("customer") Customer c) {
		List<Billing> b = repository.findByNumIdNotNullAndRmaNotNullAndRmaFalseAndCustomer(c);
		BigDecimal v = b.stream().map(Billing::getTotalValue).reduce(ZERO, BigDecimal::add);
		Billable a = toTotalValueOnlyBillable(v);
		return new ResponseEntity<>(a, OK);
	}

	@RequestMapping(path = "/badOrder", method = GET)
	public ResponseEntity<?> findBadOrderById(@RequestParam("id") Long id) {
		Billing b = repository.findByRmaFalseAndBookingId(id);
		Billable i = fromBilling.toBillable(b);
		return new ResponseEntity<>(i, OK);
	}

	@RequestMapping(path = "/badOrderReceipt", method = GET)
	public ResponseEntity<?> findBadOrderReceiptById(@RequestParam("id") Long id) {
		Billing b = repository.findByRmaFalseAndReceivingId(id);
		Billable i = fromBilling.toBillable(b);
		return new ResponseEntity<>(i, OK);
	}

	@RequestMapping(path = "/billed", method = GET)
	public ResponseEntity<?> findBilledByCustomer(@RequestParam("customer") Customer c) {
		List<Billing> l = repository.findByNumIdGreaterThanAndRmaNullAndCustomerOrderByOrderDateDesc(0L, c);
		ArrayList<Billing> noGreaterThan30dayGapInvoices = new ArrayList<>();
		for (int i = 0; i < l.size(); i++)
			if (greaterThan30DayGapBetweenInvoices(l, i))
				break;
			else
				noGreaterThan30dayGapInvoices.add(l.get(i));
		BigDecimal v = noGreaterThan30dayGapInvoices.stream().map(Billing::getTotalValue).reduce(ZERO, BigDecimal::add);
		Billable a = toTotalValueOnlyBillable(v);
		return new ResponseEntity<>(a, OK);

	}

	@RequestMapping(path = "/referenced", method = GET)
	public ResponseEntity<?> findByBookingId(@RequestParam("bookingId") Long id) {
		Billing b = repository.findByCustomerNotAndBookingIdAndRmaNull(vendor(), id);
		Billable i = idOnlyBillable(b);
		return new ResponseEntity<>(i, OK);
	}

	@RequestMapping(path = "/purchased", method = GET)
	public ResponseEntity<?> findByCustomerPurchaseItem(@RequestParam("by") Customer c, @RequestParam("item") Item i) {
		LocalDate start = now().minusDays(180L);
		Billable a = new Billable();
		if (!start.isBefore(goLive())) {
			Billing b = repository.findFirstByNumIdNotNullAndRmaNullAndCustomerAndOrderDateBetweenAndDetailsItem(c, start,
					now(), i);
			a = idOnlyBillable(b);
		}
		return new ResponseEntity<>(a, OK);
	}

	@RequestMapping(path = "/date", method = GET)
	public ResponseEntity<?> findByDate(@RequestParam("on") Date d) {
		ZonedDateTime start = startOfDay(d.toLocalDate());
		ZonedDateTime end = endOfDay(d.toLocalDate());
		Billing b = repository.findFirstByNumIdNotNullAndNumIdGreaterThanAndCreatedOnBetweenOrderByCreatedOnAsc(0L, start,
				end);
		Billable i = fromBilling.toBillable(b);
		return new ResponseEntity<>(i, OK);
	}

	@RequestMapping(path = "/item", method = GET)
	public ResponseEntity<?> findByItem(@RequestParam("id") Item i, @RequestParam("customer") Customer c,
			@RequestParam("date") Date d) {
		Billing b = repository.findByDetailsItemAndOrderDateAndCustomerAndRmaNull(i, d.toLocalDate(), c);
		Billable a = fromBilling.toBillable(b);
		return new ResponseEntity<>(a, OK);
	}

	@RequestMapping(path = "/find", method = GET)
	public ResponseEntity<?> findByOrderNo(@RequestParam("prefix") String p, @RequestParam("id") Long id,
			@RequestParam("suffix") String s) {
		Billing b = repository.findByPrefixAndSuffixAndNumId(nullIfEmpty(p), nullIfEmpty(s), id);
		Billable i = fromBilling.toBillable(b);
		return new ResponseEntity<>(i, OK);
	}

	// TODO
	@RequestMapping(path = "/deliveryReport", method = GET)
	public ResponseEntity<?> findDeliveryReportById(@RequestParam("id") Long id) {
		Billing b = repository.findByNumId(-id);
		Billable i = fromBilling.toBillable(b);
		return new ResponseEntity<>(i, OK);
	}

	@RequestMapping(path = "/notFullyPaidCOD", method = GET)
	public ResponseEntity<?> findNotFullyPaidCOD(@RequestParam("seller") String s, @RequestParam("upTo") Date d) {
		List<Billing> l = repository
				.findByNumIdNotNullAndRmaNullAndCustomerNotAndFullyPaidFalseAndOrderDateBetweenOrderByOrderDateAsc(vendor(),
						goLive(), billingCutoff(d, s));
		Optional<Billing> o = l.stream().filter(codCustomerOf(s)).findFirst();
		Billable b = o.isPresent() ? orderNoOnlyBillable(o.get()) : null;
		return new ResponseEntity<>(b, OK);
	}

	@RequestMapping(path = "/pendingReturn", method = GET)
	public ResponseEntity<?> findOpenBadOrReturnOrderByCustomer(@RequestParam("customer") Customer c) {
		List<Billing> b = repository.findByNumIdNullAndRmaNotNullAndCustomer(c);
		Billable a = removeCancelled(b);
		logger.info("Open bad or return order = " + a);
		return new ResponseEntity<>(a, OK);
	}

	private Billable removeCancelled(List<Billing> l) {
		try {
			Billing i = l.stream().filter(b -> b.getIsValid() != null && b.getIsValid() == false).findFirst().get();
			return bookingIdOnlyBillable(i);
		} catch (Exception e) {
			return null;
		}
	}

	@RequestMapping(path = "/purchaseOrder", method = GET)
	public ResponseEntity<?> findPurchaseOrderById(@RequestParam("id") Long id) {
		Billing b = repository.findByCustomerAndBookingId(vendor(), id);
		Billable i = fromBilling.toBillable(b);
		return new ResponseEntity<>(i, OK);
	}

	@RequestMapping(path = "/purchaseReceipt", method = GET)
	public ResponseEntity<?> findPurchaseReceiptById(@RequestParam("id") Long id) {
		Billing b = repository.findByCustomerAndReceivingId(vendor(), id);
		Billable i = fromBilling.toBillable(b);
		return new ResponseEntity<>(i, OK);
	}

	@RequestMapping(path = "/receivingReport", method = GET)
	public ResponseEntity<?> findReceivingReportById(@RequestParam("id") Long id) {
		Billing b = repository.findByCustomerNotAndReceivingIdAndRmaNull(vendor(), id);
		Billable i = fromBilling.toBillable(b);
		return new ResponseEntity<>(i, OK);
	}

	@RequestMapping(path = "/returnOrders", method = GET)
	public ResponseEntity<?> findReturnOrderByCustomer(@RequestParam("customer") Customer c) {
		List<Billing> b = repository.findByNumIdNotNullAndRmaNotNullAndRmaTrueAndCustomer(c);
		BigDecimal v = b.stream().map(Billing::getTotalValue).reduce(ZERO, BigDecimal::add);
		Billable a = toTotalValueOnlyBillable(v);
		return new ResponseEntity<>(a, OK);
	}

	@RequestMapping(path = "/returnOrder", method = GET)
	public ResponseEntity<?> findRmaById(@RequestParam("id") Long id) {
		Billing b = repository.findByRmaTrueAndBookingId(id);
		Billable i = fromBilling.toBillable(b);
		return new ResponseEntity<>(i, OK);
	}

	@RequestMapping(path = "/returnReceipt", method = GET)
	public ResponseEntity<?> findRmaReceiptById(@RequestParam("id") Long id) {
		Billing b = repository.findByRmaTrueAndReceivingId(id);
		Billable i = fromBilling.toBillable(b);
		return new ResponseEntity<>(i, OK);
	}

	@RequestMapping(path = "/salesOrder", method = GET)
	public ResponseEntity<?> findSalesOrderById(@RequestParam("id") Long id) {
		Billing b = repository.findByCustomerNotAndBookingIdAndRmaNull(vendor(), id);
		Billable i = fromBilling.toBillable(b);
		return new ResponseEntity<>(i, OK);
	}

	@RequestMapping(path = "/unbilled", method = GET)
	public ResponseEntity<?> findUnbilledPickedUpTo(@RequestParam("seller") String seller,
			@RequestParam("upTo") Date d) {
		List<Billing> b = repository
				.findByNumIdNullAndRmaNullAndCustomerNotAndBookingIdNotNullAndPickingNotNullAndOrderDateBetweenOrderByOrderDateAsc(
						vendor(), goLive(), billingCutoff(d, seller));
		Billable billable = b == null ? null : bookingIdOnlyBillable(b.get(0));
		if (!seller.equals("all"))
			billable = filterBySeller(b, seller);
		return new ResponseEntity<>(billable, OK);
	}

	@RequestMapping(path = "/unpicked", method = GET)
	public ResponseEntity<?> findUnpickedOn(@RequestParam("date") Date d, @RequestParam("truck") String t) {
		List<Billing> b = repository.findByOrderDateAndCustomerNotAndRmaNullAndPickingNull(d.toLocalDate(), vendor());
		List<Billable> a = fromBilling.toBillable(b);
		a = a.stream().filter(byDeliveryTypeAndValidity(t)).collect(toList());
		return new ResponseEntity<>(a, OK);
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

	@RequestMapping(path = "/latest", method = GET)
	public ResponseEntity<?> list(@RequestParam("prefix") String prefix, @RequestParam("suffix") String suffix,
			@RequestParam("start") Long start, @RequestParam("end") Long end) {
		Billing b = repository.findFirstByPrefixAndSuffixAndNumIdBetweenOrderByNumIdDesc(nullIfEmpty(prefix),
				nullIfEmpty(suffix), start, end);
		Billable i = idOnlyBillable(b);
		return new ResponseEntity<>(i, OK);
	}

	@RequestMapping(path = "/next", method = GET)
	public ResponseEntity<?> next(@RequestParam("id") Long id) {
		Billing b = repository.findFirstByNumIdNotNullAndNumIdGreaterThanAndIdGreaterThanOrderByNumIdAsc(0L, id);
		Billable i = fromBilling.toBillable(b);
		return new ResponseEntity<>(i, OK);
	}

	@RequestMapping(path = "/previous", method = GET)
	public ResponseEntity<?> previous(@RequestParam("id") Long id) {
		Billing b = repository.findFirstByNumIdNotNullAndNumIdGreaterThanAndIdLessThanOrderByNumIdDesc(0L, id);
		Billable i = fromBilling.toBillable(b);
		return new ResponseEntity<>(i, OK);
	}

	@RequestMapping(path = "/print", method = GET)
	public ResponseEntity<?> printPickList(@RequestParam("id") Long id) throws FailedPrintingException {
		Billing b = repository.findOne(id);
		print(b);
		Billable i = fromBilling.toBillable(b);
		return new ResponseEntity<>(i, OK);
	}

	@RequestMapping(method = POST)
	public ResponseEntity<?> save(@RequestBody Billable i) {
		logger.info("Converting from billable to billing");
		Billing b = fromBillable.toBilling(i);
		logger.info("Saving billing");
		b = repository.save(b);
		logger.info("Converting from billing to billable");
		i = fromBilling.toBillable(b);
		return new ResponseEntity<>(i, httpHeaders(i), CREATED);
	}

	private LocalDate billingCutoff(Date d, String seller) {
		LocalDate date = d.toLocalDate();
		if (!seller.equals("all"))
			date = date.minusDays(Long.valueOf(invoicingGracePeriod));
		if (date.getDayOfWeek() == SATURDAY)
			date = date.minusDays(1L);
		return date;
	}

	private Billable bookingIdOnlyBillable(Billing b) {
		if (b == null)
			return null;
		Billable i = new Billable();
		i.setBookingId(b.getBookingId());
		return i;
	}

	private Predicate<Billable> byDeliveryTypeAndValidity(String truck) {
		return b -> (b.getIsValid() == null || b.getIsValid()) //
				&& (truck.equals("PICK-UP") ? //
						internalSales(b) || warehouseSales(b) //
						: !internalSales(b) && !warehouseSales(b));
	}

	private Predicate<Billing> codCustomerOf(String seller) {
		return a -> a.getOrderDate().equals(a.getDueDate()) && a.getCustomer().getSeller().equals(seller);
	}

	private Billable filterBySeller(List<Billing> b, String seller) {
		Optional<Billing> o = b.stream().filter(i -> seller.equals(i.getCustomer().getSeller())).findFirst();
		return o.isPresent() ? bookingIdOnlyBillable(o.get()) : null;
	}

	private Billing firstSpun() {
		return repository.findFirstByNumIdNotNullAndNumIdGreaterThanOrderByNumIdAsc(0L);
	}

	private LocalDate getInvoiceDate(List<Billing> l, int i) {
		return l.get(i).getOrderDate();
	}

	private LocalDate goLive() {
		return toDate(goLive);
	}

	private boolean greaterThan30DayGapBetweenInvoices(List<Billing> l, int i) {
		LocalDate previous = previousInvoiceDate(l, i);
		LocalDate latest = latestInvoiceDate(l, i);
		return previous.until(latest, DAYS) > 30;
	}

	private LocalDate previousInvoiceDate(List<Billing> l, int i) {
		if (i != 0)
			i--;
		return getInvoiceDate(l, i);
	}

	private MultiValueMap<String, String> httpHeaders(Billable i) {
		URI uri = fromCurrentContextPath().path("/{id}").buildAndExpand(i.getId()).toUri();
		HttpHeaders httpHeaders = new HttpHeaders();
		httpHeaders.setLocation(uri);
		return httpHeaders;
	}

	private Billable idOnlyBillable(Billing b) {
		if (b == null)
			return null;
		Billable i = new Billable();
		i.setPrefix(b.getPrefix());
		i.setNumId(b.getNumId());
		i.setSuffix(b.getSuffix());
		return i;
	}

	private boolean internalSales(Billable b) {
		return b.getRoute().equals("OTHERS");
	}

	private Billing lastSpun() {
		return repository.findFirstByNumIdNotNullAndNumIdGreaterThanOrderByNumIdDesc(0L);
	}

	private LocalDate latestInvoiceDate(List<Billing> l, int i) {
		if (i == 0)
			return LocalDate.now();
		return getInvoiceDate(l, i);
	}

	private String nullIfEmpty(String s) {
		return s.isEmpty() ? null : s;
	}

	private Billable orderNoOnlyBillable(Billing b) {
		if (b == null)
			return null;
		Billable a = new Billable();
		a.setPrefix(b.getPrefix());
		a.setNumId(b.getNumId());
		a.setSuffix(b.getSuffix());
		return a;
	}

	private void print(Billing b) throws FailedPrintingException {
		try {
			returnedMaterialPrinter.print(b);
		} catch (Exception e) {
			e.printStackTrace();
			throw new FailedPrintingException(e.getMessage());
		}
	}

	private Billable spunIdOnlyBillable(Billing b) {
		Billable i = new Billable();
		i.setId(b.getId());
		return i;
	}

	private Billable toTotalValueOnlyBillable(BigDecimal v) {
		Billable a = new Billable();
		a.setTotalValue(v);
		return a;
	}

	private Customer vendor() {
		if (vendor == null)
			vendor = customerRepo.findOne(Long.valueOf(vendorId));
		return vendor;
	}

	private boolean warehouseSales(Billable b) {
		return b.getRoute().equals("WAREHOUSE SALES");
	}
}