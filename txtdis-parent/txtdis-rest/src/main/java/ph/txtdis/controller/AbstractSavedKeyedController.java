package ph.txtdis.controller;

import static org.springframework.http.HttpStatus.OK;
import static org.springframework.web.bind.annotation.RequestMethod.GET;

import java.io.Serializable;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import ph.txtdis.dto.Keyed;
import ph.txtdis.service.SavedKeyedService;

public abstract class AbstractSavedKeyedController //
<S extends SavedKeyedService<E, T, PK>, E extends Keyed<PK>, T extends Keyed<PK>, PK extends Serializable> //
		extends AbstractSavedController<S, T, PK> {

	@RequestMapping(path = "/{id}", method = GET)
	public ResponseEntity<?> id(@PathVariable PK id) throws Exception {
		T t = service.findByPrimaryKey(id);
		return new ResponseEntity<>(t, OK);
	}
}