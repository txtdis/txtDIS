package ph.txtdis.mgdc.gsm.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ph.txtdis.controller.AbstractSpunSavedKeyedController;
import ph.txtdis.exception.NotFoundException;
import ph.txtdis.mgdc.gsm.domain.CustomerEntity;
import ph.txtdis.mgdc.gsm.dto.Customer;
import ph.txtdis.mgdc.gsm.service.server.CreditedAndDiscountedCustomerService;

import java.util.List;

import static org.springframework.http.HttpStatus.OK;
import static org.springframework.web.bind.annotation.RequestMethod.GET;

@RequestMapping("/customers")
@RestController("customerController")
public class CustomerController //
	extends AbstractSpunSavedKeyedController<CreditedAndDiscountedCustomerService, CustomerEntity, Customer> {

	@RequestMapping(path = "/banks", method = GET)
	public ResponseEntity<?> banks() {
		List<Customer> l = service.findBanks();
		return new ResponseEntity<>(l, OK);
	}

	@RequestMapping(path = "/customer", method = GET)
	public ResponseEntity<?> customer(@RequestParam("id") Long id) throws NotFoundException {
		Customer c = service.findByPrimaryKey(id);
		return new ResponseEntity<>(c, OK);
	}

	@RequestMapping(path = "/find", method = GET)
	public ResponseEntity<?> find(@RequestParam("name") String s) {
		Customer c = service.findByName(s);
		return new ResponseEntity<>(c, OK);
	}

	@RequestMapping(method = GET)
	public ResponseEntity<?> getAll() {
		List<Customer> l = service.findAll();
		return new ResponseEntity<>(l, OK);
	}

	@RequestMapping(path = "/outlets", method = GET)
	public ResponseEntity<?> outlets() {
		List<Customer> l = service.listOutletIdsAndNames();
		return new ResponseEntity<>(l, OK);
	}

	@RequestMapping(path = "/search", method = GET)
	public ResponseEntity<?> search(@RequestParam("name") String s) {
		List<Customer> l = service.searchByName(s);
		return new ResponseEntity<>(l, OK);
	}

	@RequestMapping(path = "/employee", method = GET)
	public ResponseEntity<?> employee(@RequestParam("id") Long id) {
		Customer c = service.findEmployeeIdAndName(id);
		return new ResponseEntity<>(c, OK);
	}

	@RequestMapping(path = "/noContactDetails", method = GET)
	public ResponseEntity<?> noContactDetails() {
		Customer c = service.findNoContactDetails();
		return new ResponseEntity<>(c, OK);
	}

	@RequestMapping(path = "/noDesignation", method = GET)
	public ResponseEntity<?> noDesignation() {
		Customer c = service.findNoDesignation();
		return new ResponseEntity<>(c, OK);
	}

	@RequestMapping(path = "/noMobile", method = GET)
	public ResponseEntity<?> noMobile() {
		Customer c = service.findNoMobileNo();
		return new ResponseEntity<>(c, OK);
	}

	@RequestMapping(path = "/noStreetAddress", method = GET)
	public ResponseEntity<?> noStreetAddress() {
		Customer c = service.findNoStreetAddress();
		return new ResponseEntity<>(c, OK);
	}

	@RequestMapping(path = "/noSurname", method = GET)
	public ResponseEntity<?> noSurname() {
		Customer c = service.findNoSurname();
		return new ResponseEntity<>(c, OK);
	}

	@RequestMapping(path = "/notCorrectBarangayAddress", method = GET)
	public ResponseEntity<?> notCorrectBarangayAddress() {
		Customer c = service.findIncorrectBarangayAddress();
		return new ResponseEntity<>(c, OK);
	}

	@RequestMapping(path = "/notCorrectCityAddress", method = GET)
	public ResponseEntity<?> notCorrectCityAddress() {
		Customer c = service.findIncorrectCityAddress();
		return new ResponseEntity<>(c, OK);
	}

	@RequestMapping(path = "/notCorrectProvincialAddress", method = GET)
	public ResponseEntity<?> notCorrectProvincialAddress() {
		Customer c = service.findIncorrectProvincialAddress();
		return new ResponseEntity<>(c, OK);
	}

	@RequestMapping(path = "/notTheSameVisitFrequencyAndSchedule", method = GET)
	public ResponseEntity<?> notTheSameVisitFrequencyAndSchedule() {
		Customer c = service.findDifferentVisitFrequencyAndSchedule();
		return new ResponseEntity<>(c, OK);
	}

	@RequestMapping(path = "/notTheSameWeeksOneAndFiveVisitSchedules", method = GET)
	public ResponseEntity<?> notTheSameWeeksOneAndFiveVisitSchedules() {
		Customer c = service.findDifferentWeeksOneAndFiveVisitSchedule();
		return new ResponseEntity<>(c, OK);
	}

	@RequestMapping(path = "/noVisitSchedule", method = GET)
	public ResponseEntity<?> noVisitSchedule() {
		Customer c = service.findNoVisitSchedule();
		return new ResponseEntity<>(c, OK);
	}

	@RequestMapping(path = "/onAction", method = GET)
	public ResponseEntity<?> onAction() {
		List<Customer> l = service.findAllOnAction();
		return new ResponseEntity<>(l, OK);
	}

	@RequestMapping(path = "/route", method = GET)
	public ResponseEntity<?> route(@RequestParam("id") Long id) {
		List<Customer> l = service.findAllByScheduledRouteAndGoodCreditStanding(id);
		return new ResponseEntity<>(l, OK);
	}

	@RequestMapping(path = "/vendor", method = GET)
	public ResponseEntity<?> vendor() {
		Customer c = service.getVendor();
		return new ResponseEntity<>(c, OK);
	}
}