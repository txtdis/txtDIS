package ph.txtdis.dyvek.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ph.txtdis.controller.AbstractSpunSavedKeyedController;
import ph.txtdis.dto.Keyed;
import ph.txtdis.dyvek.service.server.SearchedSpunSavedOrderService;

import java.util.List;

import static org.springframework.http.HttpStatus.OK;
import static org.springframework.web.bind.annotation.RequestMethod.GET;

public abstract class AbstractSearchedSpunSavedOrderController< //
	S extends SearchedSpunSavedOrderService<E, T>, //
	E extends Keyed<Long>, //
	T extends Keyed<Long>> //
	extends AbstractSpunSavedKeyedController<S, E, T> {

	@RequestMapping(path = "/search", method = GET)
	public ResponseEntity<?> search(@RequestParam("orderNo") String no) {
		List<T> l = service.search(no);
		return new ResponseEntity<>(l, OK);
	}
}