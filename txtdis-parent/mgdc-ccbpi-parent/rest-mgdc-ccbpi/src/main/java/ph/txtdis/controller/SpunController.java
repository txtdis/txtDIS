package ph.txtdis.controller;

import static org.springframework.http.HttpStatus.OK;
import static org.springframework.web.bind.annotation.RequestMethod.GET;

import java.io.Serializable;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import ph.txtdis.domain.Keyed;
import ph.txtdis.repository.SpunRepository;

public abstract class SpunController<R extends SpunRepository<T, ID>, T extends Keyed<ID>, ID extends Serializable>
		extends IdController<R, T, ID>
{
	@RequestMapping(path = "/first", method = GET)
	public ResponseEntity<?> first() {
		T t = firstSpun();
		return new ResponseEntity<T>(t, OK);
	}

	@RequestMapping(path = "/firstToSpin", method = GET)
	public ResponseEntity<?> firstToSpin() {
		T t = firstSpun();
		return new ResponseEntity<T>(t, OK);
	}

	@RequestMapping(path = "/last", method = GET)
	public ResponseEntity<?> last() {
		T t = lastSpun();
		return new ResponseEntity<T>(t, OK);
	}

	@RequestMapping(path = "/lastToSpin", method = GET)
	public ResponseEntity<?> lastToSpin() {
		T t = lastSpun();
		return new ResponseEntity<T>(t, OK);
	}

	@RequestMapping(path = "/next", method = GET)
	public ResponseEntity<?> next(@RequestParam("id") ID id) {
		T t = repository.findFirstByIdGreaterThanOrderByIdAsc(id);
		return new ResponseEntity<T>(t, OK);
	}

	@RequestMapping(path = "/previous", method = GET)
	public ResponseEntity<?> previous(@RequestParam("id") ID id) {
		T t = repository.findFirstByIdLessThanOrderByIdDesc(id);
		return new ResponseEntity<T>(t, OK);
	}

	private T firstSpun() {
		return repository.findFirstByOrderByIdAsc();
	}

	private T lastSpun() {
		return repository.findFirstByOrderByIdDesc();
	}
}