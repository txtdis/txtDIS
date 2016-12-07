package ph.txtdis.controller;

import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;
import static org.springframework.web.servlet.support.ServletUriComponentsBuilder.fromCurrentContextPath;

import java.net.URI;
import java.util.List;

import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import ph.txtdis.dto.Customer;
import ph.txtdis.service.CustomerService;

@RequestMapping("/customers")
@RestController("customerController")
public class CustomerController extends AbstractSpunController<CustomerService, Customer, Long> {

	@RequestMapping(path = "/banks", method = GET)
	public ResponseEntity<?> findBanks() {
		List<Customer> l = service.findBanks();
		return new ResponseEntity<>(l, OK);
	}

	@RequestMapping(path = "/customer", method = GET)
	public ResponseEntity<?> findById(@RequestParam("id") Long id) {
		Customer c = service.find(id);
		return new ResponseEntity<>(c, OK);
	}

	@RequestMapping(path = "/exTrucks", method = GET)
	public ResponseEntity<?> findExTrucks() {
		List<Customer> l = service.findExTrucks();
		return new ResponseEntity<>(l, OK);
	}

	@RequestMapping(path = "/find", method = GET)
	public ResponseEntity<?> findByName(@RequestParam("name") String s) {
		Customer c = service.findByName(s);
		return new ResponseEntity<>(c, OK);
	}

	@RequestMapping(path = "/get", method = GET)
	public ResponseEntity<?> findByVendorId(@RequestParam("vendorId") Long id) {
		Customer c = service.findByVendorId(id);
		return new ResponseEntity<>(c, OK);
	}

	@RequestMapping(path = "/noContactDetails", method = GET)
	public ResponseEntity<?> findNoContactDetails() {
		Customer c = service.findNoContactDetails();
		return new ResponseEntity<>(c, OK);
	}

	@RequestMapping(path = "/noDesignation", method = GET)
	public ResponseEntity<?> findNoDesignation() {
		Customer c = service.findNoDesignation();
		return new ResponseEntity<>(c, OK);
	}

	@RequestMapping(path = "/noMobile", method = GET)
	public ResponseEntity<?> findNoMobileNo() {
		Customer c = service.findNoMobileNo();
		return new ResponseEntity<>(c, OK);
	}

	@RequestMapping(path = "/noStreetAddress", method = GET)
	public ResponseEntity<?> findNoStreetAddress() {
		Customer c = service.findNoStreetAddress();
		return new ResponseEntity<>(c, OK);
	}

	@RequestMapping(path = "/noSurname", method = GET)
	public ResponseEntity<?> findNoSurname() {
		Customer c = service.findNoSurname();
		return new ResponseEntity<>(c, OK);
	}

	@RequestMapping(path = "/notCorrectBarangayAddress", method = GET)
	public ResponseEntity<?> findNotCorrectBarangayAddress() {
		Customer c = service.findNotCorrectBarangayAddress();
		return new ResponseEntity<>(c, OK);
	}

	@RequestMapping(path = "/notCorrectCityAddress", method = GET)
	public ResponseEntity<?> findNotCorrectCityAddress() {
		Customer c = service.findNotCorrectCityAddress();
		return new ResponseEntity<>(c, OK);
	}

	@RequestMapping(path = "/notCorrectProvincialAddress", method = GET)
	public ResponseEntity<?> findNotCorrectProvincialAddress() {
		Customer c = service.findNotCorrectProvincialAddress();
		return new ResponseEntity<>(c, OK);
	}

	@RequestMapping(path = "/notTheSameVisitFrequencyAndSchedule", method = GET)
	public ResponseEntity<?> findNotTheSameVisitFrequencyAndSchedule() {
		Customer c = service.findNotTheSameVisitFrequencyAndSchedule();
		return new ResponseEntity<>(c, OK);
	}

	@RequestMapping(path = "/notTheSameWeeksOneAndFiveVisitSchedules", method = GET)
	public ResponseEntity<?> findNotTheSameWeeksOneAndFiveVisitSchedule() {
		Customer c = service.findNotTheSameWeeksOneAndFiveVisitSchedule();
		return new ResponseEntity<>(c, OK);
	}

	@RequestMapping(path = "/noVisitSchedule", method = GET)
	public ResponseEntity<?> findNoVisitSchedule() {
		Customer c = service.findNoVisitSchedule();
		return new ResponseEntity<>(c, OK);
	}

	@RequestMapping(path = "/vendor", method = GET)
	public ResponseEntity<?> findVendor() {
		Customer c = service.getVendor();
		return new ResponseEntity<>(c, OK);
	}

	@RequestMapping(method = GET)
	public ResponseEntity<?> list() {
		List<Customer> l = service.list();
		return new ResponseEntity<>(l, OK);
	}

	@RequestMapping(path = "/route", method = GET)
	public ResponseEntity<?> listByRoute(@RequestParam("id") Long id) {
		List<Customer> l = service.listByScheduledRouteAndGoodCreditStanding(id);
		return new ResponseEntity<>(l, OK);
	}

	@Override
	@RequestMapping(method = POST)
	public ResponseEntity<?> save(@RequestBody Customer body) {
		body = service.save(body);
		return new ResponseEntity<>(body, httpHeaders(body), CREATED);
	}

	@Override
	protected MultiValueMap<String, String> httpHeaders(Customer body) {
		URI uri = fromCurrentContextPath().path("/{id}").buildAndExpand(body.getId()).toUri();
		HttpHeaders httpHeaders = new HttpHeaders();
		httpHeaders.setLocation(uri);
		return httpHeaders;
	}

	@RequestMapping(path = "/search", method = GET)
	public ResponseEntity<?> searchByName(@RequestParam("name") String s) {
		List<Customer> l = service.searchByName(s);
		return new ResponseEntity<>(l, OK);
	}
}