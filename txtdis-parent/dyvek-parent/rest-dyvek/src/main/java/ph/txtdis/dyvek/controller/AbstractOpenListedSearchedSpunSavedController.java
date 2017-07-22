package ph.txtdis.dyvek.controller;

import static org.springframework.http.HttpStatus.OK;
import static org.springframework.web.bind.annotation.RequestMethod.GET;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;

import ph.txtdis.dto.Keyed;
import ph.txtdis.dyvek.service.server.OpenListedSearchedSpunSavedOrderService;

public abstract class AbstractOpenListedSearchedSpunSavedController< //
		S extends OpenListedSearchedSpunSavedOrderService<E, T>, //
		E extends Keyed<Long>, //
		T extends Keyed<Long>> //
		extends AbstractSearchedSpunSavedOrderController<S, E, T> {

	@RequestMapping(method = GET)
	public ResponseEntity<?> get() {
		List<T> l = service.findAllOpen();
		return new ResponseEntity<>(l, OK);
	}
}