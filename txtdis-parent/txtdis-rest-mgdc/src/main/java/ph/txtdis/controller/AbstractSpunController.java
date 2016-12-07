package ph.txtdis.controller;

import static org.springframework.http.HttpStatus.OK;
import static org.springframework.web.bind.annotation.RequestMethod.GET;

import java.io.Serializable;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import ph.txtdis.dto.Keyed;
import ph.txtdis.service.SpunService;

public abstract class AbstractSpunController< //
		S extends SpunService<T, PK>, //
		T extends Keyed<PK>, //
		PK extends Serializable> //
		extends AbstractIdController<S, T, PK> {

	@RequestMapping(path = "/first", method = GET)
	public ResponseEntity<?> first() {
		T t = service.first();
		return new ResponseEntity<>(t, OK);
	}

	@RequestMapping(path = "/firstToSpin", method = GET)
	public ResponseEntity<?> firstToSpin() {
		T t = service.firstToSpin();
		return new ResponseEntity<>(t, OK);
	}

	@RequestMapping(path = "/last", method = GET)
	public ResponseEntity<?> last() {
		T t = service.last();
		return new ResponseEntity<>(t, OK);
	}

	@RequestMapping(path = "/lastToSpin", method = GET)
	public ResponseEntity<?> lastToSpin() {
		T t = service.lastToSpin();
		return new ResponseEntity<>(t, OK);
	}

	@RequestMapping(path = "/next", method = GET)
	public ResponseEntity<?> next(@RequestParam("id") PK id) {
		T t = service.next(id);
		return new ResponseEntity<>(t, OK);
	}

	@RequestMapping(path = "/previous", method = GET)
	public ResponseEntity<?> previous(@RequestParam("id") PK id) {
		T t = service.previous(id);
		return new ResponseEntity<>(t, OK);
	}
}