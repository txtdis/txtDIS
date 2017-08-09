package ph.txtdis.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ph.txtdis.mgdc.gsm.dto.Customer;
import ph.txtdis.service.EdmsCustomerService;

import java.util.List;

import static org.springframework.http.HttpStatus.OK;
import static org.springframework.web.bind.annotation.RequestMethod.GET;

@RequestMapping("/customers")
@RestController("customerController")
public class CustomerController //
	extends AbstractSavedController<EdmsCustomerService, Customer, Long> {

	@RequestMapping(method = GET)
	public ResponseEntity<?> list() {
		List<Customer> l = service.list();
		return new ResponseEntity<>(l, OK);
	}

	@RequestMapping(path = "/onAction")
	public ResponseEntity<?> listonAction() {
		List<Customer> l = service.listonAction();
		return new ResponseEntity<>(l, OK);
	}
}