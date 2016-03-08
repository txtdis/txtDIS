package ph.txtdis.controller;

import static java.util.stream.Collectors.toList;
import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;
import static org.springframework.web.servlet.support.ServletUriComponentsBuilder.fromCurrentContextPath;

import java.net.URI;
import java.sql.Date;
import java.time.LocalDate;
import java.util.List;

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

import static ph.txtdis.util.DateTimeUtils.endOfDay;
import static ph.txtdis.util.DateTimeUtils.startOfDay;
import static ph.txtdis.util.DateTimeUtils.toDate;

import ph.txtdis.domain.Billing;
import ph.txtdis.domain.Customer;
import ph.txtdis.domain.Remittance;
import ph.txtdis.dto.Payment;
import ph.txtdis.repository.RemittanceRepository;
import ph.txtdis.service.PaymentToRemittanceService;
import ph.txtdis.service.RemittanceRestService;
import ph.txtdis.service.RemittanceToPaymentService;
import ph.txtdis.type.PaymentType;

@RestController("remittanceController")
@RequestMapping("/remittances")
public class RemittanceController {

	@Autowired
	private RemittanceRestService service;

	@Autowired
	private RemittanceToPaymentService fromRemittance;

	@Autowired
	private PaymentToRemittanceService fromPayment;

	@Autowired
	private RemittanceRepository repository;

	@Value("${go.live}")
	private String goLive;

	@RequestMapping(path = "/{id}", method = GET)
	public ResponseEntity<?> find(@PathVariable Long id) {
		Remittance r = repository.findOne(id);
		Payment p = fromRemittance.toPayment(r);
		return new ResponseEntity<>(p, OK);
	}

	@RequestMapping(path = "/find", method = GET)
	public ResponseEntity<?> findByBilling(@RequestParam("billing") Billing b) {
		List<Remittance> r = service.findByBilling(b);
		List<Payment> p = fromRemittance.toPayment(r);
		return new ResponseEntity<>(p, OK);
	}

	@RequestMapping(path = "/check", method = GET)
	public ResponseEntity<?> findByCheck(@RequestParam("bank") Customer b, @RequestParam("id") Long id) {
		List<Remittance> l = repository.findByDraweeBankAndCheckId(b, id);
		Remittance r = filterValidity(l);
		Payment p = fromRemittance.toPayment(r);
		return new ResponseEntity<>(p, OK);
	}

	@RequestMapping(path = "/collector", method = GET)
	public ResponseEntity<?> findByCollector(@RequestParam("name") String n, @RequestParam("date") Date d) {
		Remittance r = repository.findFirstByCollectorAndPaymentDateAndCheckIdNull(n, d.toLocalDate());
		Payment p = fromRemittance.toIdOnlyPayment(r);
		return new ResponseEntity<>(p, OK);
	}

	@RequestMapping(path = "/date", method = GET)
	public ResponseEntity<?> findByDate(@RequestParam("on") Date d) {
		Remittance r = repository.findFirstByCreatedOnBetweenOrderByIdAsc(startOfDay(d), endOfDay(d));
		Payment p = fromRemittance.toPayment(r);
		return new ResponseEntity<>(p, OK);
	}

	@RequestMapping(path = "/undeposited", method = GET)
	public ResponseEntity<?> findByUndepositedPayments(@RequestParam("payType") PaymentType t,
			@RequestParam("seller") String s, @RequestParam("upTo") Date d) {
		Remittance r = service.findOneUndepositedPayment(t, s, d.toLocalDate());
		Payment p = fromRemittance.toPayment(r);
		return new ResponseEntity<>(p, OK);
	}

	@RequestMapping(path = "/first", method = GET)
	public ResponseEntity<?> first() {
		Remittance r = repository.findFirstByOrderByIdAsc();
		Payment p = fromRemittance.toPayment(r);
		return new ResponseEntity<>(p, OK);
	}

	@RequestMapping(path = "/firstToSpin", method = GET)
	public ResponseEntity<?> firstToSpin() {
		Remittance r = repository.findFirstByOrderByIdAsc();
		Payment p = fromRemittance.toIdOnlyPayment(r);
		return new ResponseEntity<>(p, OK);
	}

	@RequestMapping(path = "/last", method = GET)
	public ResponseEntity<?> last() {
		Remittance r = repository.findFirstByOrderByIdDesc();
		Payment p = fromRemittance.toPayment(r);
		return new ResponseEntity<>(p, OK);
	}

	@RequestMapping(path = "/lastToSpin", method = GET)
	public ResponseEntity<?> lastToSpin() {
		Remittance r = repository.findFirstByOrderByIdDesc();
		Payment p = fromRemittance.toIdOnlyPayment(r);
		return new ResponseEntity<>(p, OK);
	}

	@RequestMapping(method = GET)
	public ResponseEntity<?> list() {
		LocalDate goLive = toDate(this.goLive);
		List<Remittance> l = repository
				.findByPaymentDateGreaterThanAndDepositedOnNullOrDecidedOnNullOrderByIdDesc(goLive);
		List<Payment> p = l.stream().map(r -> fromRemittance.toForHistoryPayment(r)).collect(toList());
		return new ResponseEntity<>(p, OK);
	}

	@RequestMapping(path = "/next", method = GET)
	public ResponseEntity<?> next(@RequestParam("id") Long id) {
		Remittance r = repository.findFirstByIdGreaterThanOrderByIdAsc(id);
		Payment p = fromRemittance.toPayment(r);
		return new ResponseEntity<>(p, OK);
	}

	@RequestMapping(path = "/previous", method = GET)
	public ResponseEntity<?> previous(@RequestParam("id") Long id) {
		Remittance r = repository.findFirstByIdLessThanOrderByIdDesc(id);
		Payment p = fromRemittance.toPayment(r);
		return new ResponseEntity<>(p, OK);
	}

	@RequestMapping(path = "/all", method = POST)
	public ResponseEntity<?> save(@RequestBody List<Payment> p) {
		List<Remittance> r = fromPayment.toRemittance(p);
		if (repository.save(r) == null)
			p = null;
		return new ResponseEntity<>(p, CREATED);
	}

	@RequestMapping(method = POST)
	public ResponseEntity<?> save(@RequestBody Payment p) {
		Remittance r = fromPayment.toRemittance(p);
		r = repository.save(r);
		p = fromRemittance.toPayment(r);
		return new ResponseEntity<>(p, httpHeaders(p), CREATED);
	}

	private Remittance filterValidity(List<Remittance> l) {
		try {
			return l.stream().filter(p -> p.getIsValid() == null || p.getIsValid()).findFirst().get();
		} catch (Exception e) {
			return null;
		}
	}

	private MultiValueMap<String, String> httpHeaders(Payment p) {
		URI uri = fromCurrentContextPath().path("/{id}").buildAndExpand(p.getId()).toUri();
		HttpHeaders httpHeaders = new HttpHeaders();
		httpHeaders.setLocation(uri);
		return httpHeaders;
	}
}