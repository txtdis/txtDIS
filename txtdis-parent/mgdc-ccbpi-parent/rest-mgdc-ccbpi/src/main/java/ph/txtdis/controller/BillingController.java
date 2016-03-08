package ph.txtdis.controller;

import static java.time.LocalDate.now;
import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import ph.txtdis.domain.Billing;
import ph.txtdis.repository.BillingRepository;

@RequestMapping("/billings")
@RestController("billingController")
public class BillingController extends SpunController<BillingRepository, Billing, Long> {

	@RequestMapping(path = "/imported", method = GET)
	public ResponseEntity<?> findByDate() {
		Billing b = repository.findFirstByOrderDateOrderByIdAsc(now());
		return new ResponseEntity<>(b, OK);
	}

	@RequestMapping(path = "/all", method = POST)
	public ResponseEntity<?> save(@RequestBody List<Billing> b) {
		if (repository.save(b) == null)
			b = null;
		return new ResponseEntity<>(b, CREATED);
	}
}