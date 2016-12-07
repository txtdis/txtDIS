package ph.txtdis.controller;

import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;
import static org.springframework.web.servlet.support.ServletUriComponentsBuilder.fromCurrentContextPath;

import java.net.URI;
import java.sql.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import ph.txtdis.dto.Billable;
import ph.txtdis.service.SpunService;
import ph.txtdis.service.StartEndDate;

public abstract class AbstractBillableController<S extends SpunService<Billable, Long>> {

	@Autowired
	protected S service;

	@RequestMapping(path = "/date", method = GET)
	public ResponseEntity<?> findByDate(@RequestParam("on") Date d) {
		Billable i = ((StartEndDate) service).findByDate(d);
		return new ResponseEntity<>(i, OK);
	}

	@RequestMapping(path = "/first", method = GET)
	public ResponseEntity<?> first() {
		Billable i = service.first();
		return new ResponseEntity<>(i, OK);
	}

	@RequestMapping(path = "/firstToSpin", method = GET)
	public ResponseEntity<?> firstToSpin() {
		Billable i = service.firstToSpin();
		return new ResponseEntity<>(i, OK);
	}

	@RequestMapping(path = "/last", method = GET)
	public ResponseEntity<?> last() {
		Billable i = service.last();
		return new ResponseEntity<>(i, OK);
	}

	@RequestMapping(path = "/lastToSpin", method = GET)
	public ResponseEntity<?> lastToSpin() {
		Billable i = service.lastToSpin();
		return new ResponseEntity<>(i, OK);
	}

	@RequestMapping(path = "/next", method = GET)
	public ResponseEntity<?> next(@RequestParam("id") Long id) {
		Billable i = service.next(id);
		return new ResponseEntity<>(i, OK);
	}

	@RequestMapping(path = "/previous", method = GET)
	public ResponseEntity<?> previous(@RequestParam("id") Long id) {
		Billable i = service.previous(id);
		return new ResponseEntity<>(i, OK);
	}

	@RequestMapping(method = POST)
	public ResponseEntity<?> save(@RequestBody Billable body) {
		body = service.save(body);
		return new ResponseEntity<>(body, httpHeaders(body), CREATED);
	}

	private MultiValueMap<String, String> httpHeaders(Billable body) {
		URI uri = fromCurrentContextPath().path("/{id}").buildAndExpand(body.getId()).toUri();
		HttpHeaders httpHeaders = new HttpHeaders();
		httpHeaders.setLocation(uri);
		return httpHeaders;
	}
}