package ph.txtdis.mgdc.ccbpi.controller;

import static org.springframework.http.HttpStatus.OK;
import static org.springframework.web.bind.annotation.RequestMethod.GET;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import ph.txtdis.controller.AbstractSpunSavedKeyedController;
import ph.txtdis.exception.NotFoundException;
import ph.txtdis.mgdc.ccbpi.domain.CustomerEntity;
import ph.txtdis.mgdc.ccbpi.dto.Customer;
import ph.txtdis.mgdc.ccbpi.service.server.CustomerService;

@RequestMapping("/customers")
@RestController("customerController")
public class CustomerController //
	extends AbstractSpunSavedKeyedController<CustomerService, CustomerEntity, Customer> {

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

	@RequestMapping(path = "/get", method = GET)
	public ResponseEntity<?> get(@RequestParam("vendorId") Long id) {
		Customer c = service.findByVendorId(id);
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
}