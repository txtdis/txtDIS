package ph.txtdis.mgdc.gsm.controller;

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
import ph.txtdis.mgdc.gsm.service.server.StockTakeService;

@RestController("StockTakingController")
@RequestMapping("/stockTakes")
public class StockTakeController {

	@Autowired
	private StockTakeService service;

	@RequestMapping(path = "/{id}", method = GET)
	public ResponseEntity<?> find(@PathVariable Long id) {
		StockTake s = service.find(id);
		return new ResponseEntity<>(s, OK);
	}

	@RequestMapping(path = "/count", method = GET)
	public ResponseEntity<?> findByWarehouseAndDate(@RequestParam("warehouse") String w, @RequestParam("date") Date d) {
		StockTake s = service.findByWarehouseAndDate(w, d.toLocalDate());
		return new ResponseEntity<>(s, OK);
	}

	@RequestMapping(path = "/date", method = GET)
	public ResponseEntity<?> findByDate(@RequestParam("on") Date d) throws Exception {
		StockTake s = service.findByDate(d.toLocalDate());
		return new ResponseEntity<>(s, OK);
	}

	@RequestMapping(path = "/first", method = GET)
	public ResponseEntity<?> first() {
		StockTake s = service.first();
		return new ResponseEntity<>(s, OK);
	}

	@RequestMapping(path = "/firstToSpin", method = GET)
	public ResponseEntity<?> firstToSpin() {
		StockTake s = service.firstToSpin();
		return new ResponseEntity<>(s, OK);
	}

	@RequestMapping(path = "/last", method = GET)
	public ResponseEntity<?> last() {
		StockTake s = service.last();
		return new ResponseEntity<>(s, OK);
	}

	@RequestMapping(path = "/lastToSpin", method = GET)
	public ResponseEntity<?> lastToSpin() {
		StockTake s = service.lastToSpin();
		return new ResponseEntity<>(s, OK);
	}

	@RequestMapping(path = "/next", method = GET)
	public ResponseEntity<?> next(@RequestParam("id") Long id) {
		StockTake s = service.next(id);
		return new ResponseEntity<>(s, OK);
	}

	@RequestMapping(path = "/previous", method = GET)
	public ResponseEntity<?> previous(@RequestParam("id") Long id) {
		StockTake s = service.previous(id);
		return new ResponseEntity<>(s, OK);
	}

	@RequestMapping(method = POST)
	public ResponseEntity<?> save(@RequestBody StockTake s) {
		s = service.save(s);
		return new ResponseEntity<>(s, httpHeaders(s), CREATED);
	}

	private MultiValueMap<String, String> httpHeaders(StockTake s) {
		URI uri = fromCurrentContextPath().path("/{id}").buildAndExpand(s.getId()).toUri();
		HttpHeaders h = new HttpHeaders();
		h.setLocation(uri);
		return h;
	}
}