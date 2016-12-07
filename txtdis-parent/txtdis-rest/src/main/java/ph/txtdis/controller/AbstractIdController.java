package ph.txtdis.controller;

import static org.springframework.http.HttpStatus.OK;
import static org.springframework.web.bind.annotation.RequestMethod.GET;

import java.io.Serializable;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import ph.txtdis.dto.Keyed;
import ph.txtdis.exception.NotFoundException;
import ph.txtdis.service.IdService;

public abstract class AbstractIdController< //
		S extends IdService<T, PK>, //
		T extends Keyed<PK>, //
		PK extends Serializable> //
		extends AbstractCreateController<S, T, PK> {

	@RequestMapping(path = "/{id}", method = GET)
	public ResponseEntity<?> find(@PathVariable PK id) throws NotFoundException {
		T t = service.findById(id);
		return new ResponseEntity<>(t, OK);
	}

	@Override
	protected String pathName() {
		return "/{id}";
	}

	@Override
	protected Object pathObject(T body) {
		return body.getId();
	}
}