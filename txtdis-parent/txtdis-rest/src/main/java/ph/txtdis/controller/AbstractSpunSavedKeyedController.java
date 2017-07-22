package ph.txtdis.controller;

import static org.springframework.http.HttpStatus.OK;
import static org.springframework.web.bind.annotation.RequestMethod.GET;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import ph.txtdis.dto.Keyed;
import ph.txtdis.service.SpunSavedKeyedService;

public abstract class AbstractSpunSavedKeyedController< //
		S extends SpunSavedKeyedService<E, T, Long>, //
		E extends Keyed<Long>, //
		T extends Keyed<Long>> //
		extends AbstractSavedKeyedController<S, E, T, Long> {

	@RequestMapping(path = "/next", method = GET)
	public ResponseEntity<?> next(@RequestParam("id") String id) {
		T t = service.next(key(id));
		return new ResponseEntity<>(t, OK);
	}

	private Long key(String key) {
		try {
			return Long.valueOf(key);
		} catch (NumberFormatException e) {
			return null;
		}
	}

	@RequestMapping(path = "/previous", method = GET)
	public ResponseEntity<?> previous(@RequestParam("id") String id) {
		T t = service.previous(key(id));
		return new ResponseEntity<>(t, OK);
	}
}