package ph.txtdis.controller;

import static org.springframework.http.HttpStatus.OK;
import static org.springframework.web.bind.annotation.RequestMethod.GET;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import ph.txtdis.dto.Named;
import ph.txtdis.service.NameListCreateService;

public abstract class AbstractNameListController<S extends NameListCreateService<T>, T extends Named>
		extends AbstractCreateController<S, T, Long> {

	@RequestMapping(path = "/{name}", method = GET)
	public ResponseEntity<?> find(@PathVariable String name) {
		T body = service.findByName(name);
		return new ResponseEntity<>(body, HttpStatus.OK);
	}

	@RequestMapping(method = GET)
	public ResponseEntity<?> list() {
		List<T> list = service.findByOrderByNameAsc();
		return new ResponseEntity<>(list, OK);
	}

	@Override
	protected String pathName() {
		return "/{name}";
	}

	@Override
	protected Object pathObject(T body) {
		return body.getName();
	}
}