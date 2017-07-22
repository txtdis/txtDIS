package ph.txtdis.mgdc.controller;

import static org.springframework.http.HttpStatus.OK;
import static org.springframework.web.bind.annotation.RequestMethod.GET;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import ph.txtdis.controller.AbstractSpunSavedKeyedController;
import ph.txtdis.dto.Keyed;
import ph.txtdis.exception.NotFoundException;
import ph.txtdis.mgdc.service.server.SpunSavedReferencedKeyedService;

public abstract class AbstractSpunSavedReferencedKeyedController< //
		S extends SpunSavedReferencedKeyedService<E, T, Long>, //
		E extends Keyed<Long>, //
		T extends Keyed<Long>> //
		extends AbstractSpunSavedKeyedController<S, E, T> {

	@RequestMapping(path = "/find", method = GET)
	public ResponseEntity<?> find(@RequestParam("id") String key) throws NotFoundException {
		try {
			T t = service.findByReferenceId(Long.valueOf(key));
			return new ResponseEntity<>(t, OK);
		} catch (NumberFormatException e) {
			throw new NotFoundException("ID No. " + key);
		}
	}
}