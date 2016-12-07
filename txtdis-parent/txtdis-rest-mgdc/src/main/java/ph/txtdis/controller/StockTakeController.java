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
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import ph.txtdis.dto.StockTake;
import ph.txtdis.service.StockTakeService;

@RestController("StockTakingController")
@RequestMapping("/stockTakes")
public class StockTakeController {

	@Autowired
	private StockTakeService service;

	@RequestMapping(path = "/{id}", method = GET)
	public ResponseEntity<?> find(@PathVariable Long id) {
		StockTake a = service.find(id);
		return new ResponseEntity<>(a, OK);
	}

	@RequestMapping(path = "/count", method = GET)
	public ResponseEntity<?> findByWarehouseAndDate(@RequestParam("warehouse") String w, @RequestParam("date") Date d) {
		StockTake s = service.findByWarehouseAndDate(w, d);
		return new ResponseEntity<>(s, OK);
	}

	@RequestMapping(path = "/date", method = GET)
	public ResponseEntity<?> findByDate(@RequestParam("on") Date d) {
		StockTake a = service.findByDate(d);
		return new ResponseEntity<>(a, OK);
	}

	@RequestMapping(path = "/first", method = GET)
	public ResponseEntity<?> first() {
		StockTake a = service.first();
		return new ResponseEntity<>(a, OK);
	}

	@RequestMapping(path = "/firstToSpin", method = GET)
	public ResponseEntity<?> firstToSpin() {
		StockTake a = service.firstToSpin();
		return new ResponseEntity<>(a, OK);
	}

	@RequestMapping(path = "/last", method = GET)
	public ResponseEntity<?> last() {
		StockTake a = service.last();
		return new ResponseEntity<>(a, OK);
	}

	@RequestMapping(path = "/lastToSpin", method = GET)
	public ResponseEntity<?> lastToSpin() {
		StockTake a = service.lastToSpin();
		return new ResponseEntity<>(a, OK);
	}

	@RequestMapping(path = "/next", method = GET)
	public ResponseEntity<?> next(@RequestParam("id") Long id) {
		StockTake a = service.next(id);
		return new ResponseEntity<>(a, OK);
	}

	@RequestMapping(path = "/previous", method = GET)
	public ResponseEntity<?> previous(@RequestParam("id") Long id) {
		StockTake a = service.previous(id);
		return new ResponseEntity<>(a, OK);
	}

	@RequestMapping(method = POST)
	public ResponseEntity<?> save(@RequestBody StockTake a) {
		a = service.save(a);
		return new ResponseEntity<>(a, httpHeaders(a), CREATED);
	}

	private MultiValueMap<String, String> httpHeaders(StockTake a) {
		URI uri = fromCurrentContextPath().path("/{id}").buildAndExpand(a.getId()).toUri();
		HttpHeaders httpHeaders = new HttpHeaders();
		httpHeaders.setLocation(uri);
		return httpHeaders;
	}
}