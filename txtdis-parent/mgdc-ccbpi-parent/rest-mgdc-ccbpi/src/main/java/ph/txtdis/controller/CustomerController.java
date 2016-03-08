package ph.txtdis.controller;

import static org.springframework.http.HttpStatus.OK;
import static org.springframework.web.bind.annotation.RequestMethod.GET;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import ph.txtdis.domain.Customer;
import ph.txtdis.repository.CustomerRepository;

@RequestMapping("/customers")
@RestController("customerController")
public class CustomerController extends NameListController<CustomerRepository, Customer> {

	@Override
	@RequestMapping(method = GET)
	public ResponseEntity<?> list() {
		List<Customer> l = (List<Customer>) repository.findAll();
		return new ResponseEntity<>(l, OK);
	}
}