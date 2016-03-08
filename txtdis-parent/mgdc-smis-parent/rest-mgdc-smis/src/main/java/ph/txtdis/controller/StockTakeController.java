package ph.txtdis.controller;

import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;
import static org.springframework.web.servlet.support.ServletUriComponentsBuilder.fromCurrentContextPath;

import java.net.URI;
import java.sql.Date;
import java.time.ZonedDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import static ph.txtdis.util.DateTimeUtils.endOfDay;
import static ph.txtdis.util.DateTimeUtils.startOfDay;

import ph.txtdis.domain.StockTaking;
import ph.txtdis.dto.StockTake;
import ph.txtdis.repository.StockTakeRepository;
import ph.txtdis.service.StockTakeToStockTakingService;
import ph.txtdis.service.StockTakingToStockTakeService;

@RestController("StockTakingController")
@RequestMapping("/stockTakes")
public class StockTakeController {

	@Autowired
	private StockTakingToStockTakeService fromStockTaking;

	@Autowired
	private StockTakeToStockTakingService fromStockTake;

	@Autowired
	private StockTakeRepository repository;

	@RequestMapping(path = "/{id}", method = GET)
	public ResponseEntity<?> find(@PathVariable Long id) {
		StockTaking i = repository.findOne(id);
		StockTake a = fromStockTaking.toStockTake(i);
		return new ResponseEntity<>(a, OK);
	}

	@RequestMapping(path = "/date", method = GET)
	public ResponseEntity<?> findByDate(@RequestParam("on") Date d) {
		ZonedDateTime start = startOfDay(d.toLocalDate());
		ZonedDateTime end = endOfDay(d.toLocalDate());
		StockTaking i = repository.findFirstByCreatedOnBetweenOrderByIdAsc(start, end);
		StockTake a = fromStockTaking.toStockTake(i);
		return new ResponseEntity<>(a, OK);
	}

	@RequestMapping(path = "/first", method = GET)
	public ResponseEntity<?> first() {
		StockTaking i = repository.findFirstByOrderByIdAsc();
		StockTake a = fromStockTaking.toStockTake(i);
		return new ResponseEntity<>(a, OK);
	}

	@RequestMapping(path = "/firstToSpin", method = GET)
	public ResponseEntity<?> firstToSpin() {
		StockTaking i = firstSpun();
		StockTake a = spunIdOnlyStockTake(i);
		return new ResponseEntity<>(a, OK);
	}

	@RequestMapping(path = "/last", method = GET)
	public ResponseEntity<?> last() {
		StockTaking i = repository.findFirstByOrderByIdDesc();
		StockTake a = fromStockTaking.toStockTake(i);
		return new ResponseEntity<>(a, OK);
	}

	@RequestMapping(path = "/lastToSpin", method = GET)
	public ResponseEntity<?> lastToSpin() {
		StockTaking i = lastSpun();
		StockTake a = spunIdOnlyStockTake(i);
		return new ResponseEntity<>(a, OK);
	}

	@RequestMapping(path = "/next", method = GET)
	public ResponseEntity<?> next(@RequestParam("id") Long id) {
		StockTaking i = repository.findFirstByIdGreaterThanOrderByIdAsc(id);
		StockTake a = fromStockTaking.toStockTake(i);
		return new ResponseEntity<>(a, OK);
	}

	@RequestMapping(path = "/previous", method = GET)
	public ResponseEntity<?> previous(@RequestParam("id") Long id) {
		StockTaking i = repository.findFirstByIdLessThanOrderByIdDesc(id);
		StockTake a = fromStockTaking.toStockTake(i);
		return new ResponseEntity<>(a, OK);
	}

	@RequestMapping(method = POST)
	public ResponseEntity<?> save(@RequestBody StockTake a) {
		StockTaking i = fromStockTake.toStockTaking(a);
		i = repository.save(i);
		a = fromStockTaking.toStockTake(i);
		return new ResponseEntity<>(a, httpHeaders(a), CREATED);
	}

	private StockTaking firstSpun() {
		return repository.findFirstByOrderByIdAsc();
	}

	private MultiValueMap<String, String> httpHeaders(StockTake a) {
		URI uri = fromCurrentContextPath().path("/{id}").buildAndExpand(a.getId()).toUri();
		HttpHeaders httpHeaders = new HttpHeaders();
		httpHeaders.setLocation(uri);
		return httpHeaders;
	}

	private StockTaking lastSpun() {
		return repository.findFirstByOrderByIdDesc();
	}

	private StockTake spunIdOnlyStockTake(StockTaking i) {
		StockTake a = new StockTake();
		a.setId(i.getId());
		return a;
	}
}