package ph.txtdis.controller;

import static java.time.ZonedDateTime.now;
import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;
import static org.springframework.web.servlet.support.ServletUriComponentsBuilder.fromCurrentContextPath;
import static ph.txtdis.util.SpringUtils.username;

import java.net.URI;
import java.sql.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import ph.txtdis.domain.Picking;
import ph.txtdis.dto.PickList;
import ph.txtdis.exception.FailedPrintingException;
import ph.txtdis.printer.PickListPrinter;
import ph.txtdis.repository.PickListRepository;
import ph.txtdis.service.PickListToPickingService;
import ph.txtdis.service.PickingToPickListService;

@RestController("pickListController")
@RequestMapping("/pickLists")
public class PickListController {

	@Autowired
	private PickListPrinter pickListPrinter;

	@Autowired
	private PickListRepository pickListRepository;

	@Autowired
	private PickingToPickListService fromPicking;

	@Autowired
	private PickListToPickingService fromPickList;

	@RequestMapping(path = "/{id}", method = GET)
	public ResponseEntity<?> find(@PathVariable Long id) {
		Picking p = pickListRepository.findOne(id);
		PickList l = fromPicking.toPickList(p);
		return new ResponseEntity<>(l, OK);
	}

	@RequestMapping(path = "/date", method = GET)
	public ResponseEntity<?> findByDate(@RequestParam("on") Date on) {
		List<Picking> p = pickListRepository.findByPickDate(on.toLocalDate());
		List<PickList> l = fromPicking.toPickList(p);
		return new ResponseEntity<>(l, OK);
	}

	@RequestMapping(path = "/first", method = GET)
	public ResponseEntity<?> first() {
		Picking b = firstSpun();
		PickList i = fromPicking.toPickList(b);
		return new ResponseEntity<>(i, OK);
	}

	@RequestMapping(path = "/firstToSpin", method = GET)
	public ResponseEntity<?> firstToSpin() {
		Picking p = firstSpun();
		PickList i = spunIdOnlyPickList(p);
		return new ResponseEntity<>(i, OK);
	}

	@RequestMapping(path = "/last", method = GET)
	public ResponseEntity<?> last() {
		Picking b = lastSpun();
		PickList i = fromPicking.toPickList(b);
		return new ResponseEntity<>(i, OK);
	}

	@RequestMapping(path = "/lastToSpin", method = GET)
	public ResponseEntity<?> lastToSpin() {
		Picking p = lastSpun();
		PickList i = spunIdOnlyPickList(p);
		return new ResponseEntity<>(i, OK);
	}

	@RequestMapping(path = "/next", method = GET)
	public ResponseEntity<?> next(@RequestParam("id") Long id) {
		Picking b = pickListRepository.findFirstByIdGreaterThanOrderByIdAsc(id);
		PickList i = fromPicking.toPickList(b);
		return new ResponseEntity<>(i, OK);
	}

	@RequestMapping(path = "/previous", method = GET)
	public ResponseEntity<?> previous(@RequestParam("id") Long id) {
		Picking b = pickListRepository.findFirstByIdLessThanOrderByIdDesc(id);
		PickList i = fromPicking.toPickList(b);
		return new ResponseEntity<>(i, OK);
	}

	@RequestMapping(path = "/print", method = GET)
	public ResponseEntity<?> printPickList(@RequestParam("id") Long id) throws Exception {
		Picking p = pickListRepository.findOne(id);
		printPickList(p);
		PickList l = fromPicking.toPickList(p);
		return new ResponseEntity<>(l, OK);
	}

	@RequestMapping(method = POST)
	public ResponseEntity<?> save(@RequestBody PickList i) {
		Picking b = fromPickList.toPicking(i);
		b = pickListRepository.save(b);
		i = fromPicking.toPickList(b);
		return new ResponseEntity<>(i, httpHeaders(i), CREATED);
	}

	private Picking firstSpun() {
		return pickListRepository.findFirstByOrderByIdAsc();
	}

	private MultiValueMap<String, String> httpHeaders(PickList i) {
		URI uri = fromCurrentContextPath().path("/{id}").buildAndExpand(i.getId()).toUri();
		HttpHeaders h = new HttpHeaders();
		h.setLocation(uri);
		return h;
	}

	private Picking lastSpun() {
		return pickListRepository.findFirstByOrderByIdDesc();
	}

	private void printLoadOrder(Picking p) throws Exception {
		// TODO
		pickListPrinter.print(p);
		setPrintData(p);
		pickListRepository.save(p);
	}

	private void printPickList(Picking p) throws FailedPrintingException {
		try {
			printLoadOrder(p);
		} catch (Exception e) {
			e.printStackTrace();
			throw new FailedPrintingException(e.getMessage());
		}
	}

	private void setPrintData(Picking p) {
		p.setPrintedBy(username());
		p.setPrintedOn(now());
	}

	private PickList spunIdOnlyPickList(Picking p) {
		PickList l = new PickList();
		l.setId(p.getId());
		return l;
	}
}