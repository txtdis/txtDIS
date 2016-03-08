package ph.txtdis.controller;

import static org.springframework.http.HttpStatus.OK;
import static org.springframework.web.bind.annotation.RequestMethod.GET;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import ph.txtdis.domain.Named;
import ph.txtdis.repository.NameListRepository;

public abstract class NameListController<R extends NameListRepository<T>, T extends Named<?>>
		extends CreateController<R, T, Long>
{

	@RequestMapping(path = "/{name}", method = GET)
	public ResponseEntity<?> find(@PathVariable String name) {
		T entity = repository.findByName(name);
		return new ResponseEntity<>(entity, HttpStatus.OK);
	}

	@RequestMapping(method = GET)
	public ResponseEntity<?> list() {
		List<T> list = repository.findByOrderByNameAsc();
		return new ResponseEntity<>(list, OK);
	}

	@Override
	protected String pathName() {
		return "/{name}";
	}

	@Override
	protected Object pathObject(T entity) {
		return entity.getName();
	}
}