package ph.txtdis.controller;

import static java.time.LocalDate.now;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.web.bind.annotation.RequestMethod.GET;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import ph.txtdis.domain.Billing;
import ph.txtdis.repository.BillingRepository;

@RequestMapping("/salesOrders")
@RestController("salesOrderController")
public class SalesOrderController {

	@Autowired
	private BillingRepository repository;

	@RequestMapping(path = "/first", method = GET)
	public ResponseEntity<?> first() {
		Billing b = firstSpun();
		return new ResponseEntity<>(b, OK);
	}

	@RequestMapping(path = "/firstToSpin", method = GET)
	public ResponseEntity<?> firstToSpin() {
		Billing b = firstSpun();
		return new ResponseEntity<>(b, OK);
	}

	@RequestMapping(path = "/last", method = GET)
	public ResponseEntity<?> last() {
		Billing b = lastSpun();
		return new ResponseEntity<>(b, OK);
	}

	@RequestMapping(path = "/lastToSpin", method = GET)
	public ResponseEntity<?> lastToSpin() {
		Billing b = lastSpun();
		return new ResponseEntity<>(b, OK);
	}

	@RequestMapping(path = "/next", method = GET)
	public ResponseEntity<?> next(@RequestParam("id") Long id) {
		Billing b = repository.findFirstByOrderDateAndIdGreaterThanOrderByIdAsc(now(), id);
		return new ResponseEntity<>(b, OK);
	}

	@RequestMapping(path = "/previous", method = GET)
	public ResponseEntity<?> previous(@RequestParam("id") Long id) {
		Billing b = repository.findFirstByOrderDateAndIdLessThanOrderByIdDesc(now(), id);
		return new ResponseEntity<>(b, OK);
	}

	private Billing firstSpun() {
		return repository.findFirstByOrderDateOrderByIdAsc(now());
	}

	private Billing lastSpun() {
		return repository.findFirstByOrderDateOrderByIdDesc(now());
	}
}