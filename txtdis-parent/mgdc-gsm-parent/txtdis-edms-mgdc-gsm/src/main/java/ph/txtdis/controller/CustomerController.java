package ph.txtdis.controller;

import static org.springframework.http.HttpStatus.OK;
import static org.springframework.web.bind.annotation.RequestMethod.GET;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import ph.txtdis.dto.Customer;
import ph.txtdis.service.CustomerService;

@RequestMapping("/customers")
@RestController("customerController")
public class CustomerController extends AbstractIdController<CustomerService, Customer, Long> {

	@RequestMapping(method = GET)
	public ResponseEntity<?> list() {
		List<Customer> l = service.list();
		return new ResponseEntity<>(l, OK);
	}

	@RequestMapping(path = "/exTrucks")
	public ResponseEntity<?> listExTrucks() {
		List<Customer> l = service.listExTrucks();
		return new ResponseEntity<>(l, OK);
	}
}