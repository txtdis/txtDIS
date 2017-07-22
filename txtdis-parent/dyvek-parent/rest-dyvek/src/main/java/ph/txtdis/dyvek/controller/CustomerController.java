package ph.txtdis.dyvek.controller;

import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import ph.txtdis.controller.AbstractNameListController;
import ph.txtdis.dyvek.model.Customer;
import ph.txtdis.dyvek.service.server.CustomerService;

@RequestMapping("/customers")
@RestController("customerController")
public class CustomerController //
		extends AbstractNameListController<CustomerService, Customer> {

	@RequestMapping(path = "/banks", method = GET)
	public ResponseEntity<?> banks() {
		List<Customer> l = service.findAllBanks();
		return new ResponseEntity<>(l, OK);
	}

	@RequestMapping(path = "/tradingPartners", method = GET)
	public ResponseEntity<?> customers() {
		List<Customer> l = service.findAllTradingPartners();
		return new ResponseEntity<>(l, OK);
	}

	@Override
	@RequestMapping(method = POST)
	public ResponseEntity<?> save(@RequestBody Customer body) {
		body = service.save(body);
		return new ResponseEntity<>(body, httpHeaders(body), CREATED);
	}

	@RequestMapping(path = "/tradingClients", method = GET)
	public ResponseEntity<?> tradingClients() {
		List<Customer> l = service.findAllTradingClients();
		return new ResponseEntity<>(l, OK);
	}

	@RequestMapping(path = "/truckingClients", method = GET)
	public ResponseEntity<?> truckingClients() {
		List<Customer> l = service.findAllTruckingClients();
		return new ResponseEntity<>(l, OK);
	}

	@RequestMapping(path = "/vendors", method = GET)
	public ResponseEntity<?> vendors() {
		List<Customer> l = service.findAllVendors();
		return new ResponseEntity<>(l, OK);
	}
}