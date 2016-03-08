package ph.txtdis.controller;

import static org.springframework.http.HttpStatus.OK;
import static org.springframework.web.bind.annotation.RequestMethod.GET;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import ph.txtdis.domain.Named;
import ph.txtdis.repository.NameListRepository;

public abstract class NameListController<R extends NameListRepository<T>, T extends Named<?>>
		extends CreateController<R, T, Long>
{

	@RequestMapping(path = "/code", method = GET)
	public ResponseEntity<?> findByCode(@RequestParam("of") Long id) {
		T t = repository.findByCode(id);
		return new ResponseEntity<>(t, OK);
	}

	@RequestMapping(path = "/name", method = GET)
	public ResponseEntity<?> findByName(@RequestParam("of") String s) {
		T t = repository.findByName(s);
		return new ResponseEntity<>(t, OK);
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