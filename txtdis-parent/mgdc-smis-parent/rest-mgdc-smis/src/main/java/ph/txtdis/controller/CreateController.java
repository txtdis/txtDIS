package ph.txtdis.controller;

import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.web.bind.annotation.RequestMethod.POST;
import static org.springframework.web.servlet.support.ServletUriComponentsBuilder.fromCurrentContextPath;

import java.io.Serializable;
import java.net.URI;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.CrudRepository;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

public abstract class CreateController<R extends CrudRepository<T, ID>, T, ID extends Serializable> {

	@Autowired
	R repository;

	@RequestMapping(method = POST)
	public ResponseEntity<?> save(@RequestBody T body) {
		T entity = repository.save(body);
		return new ResponseEntity<T>(entity, httpHeaders(uri(entity)), CREATED);
	}

	protected MultiValueMap<String, String> httpHeaders(URI uri) {
		HttpHeaders httpHeaders = new HttpHeaders();
		httpHeaders.setLocation(uri);
		return httpHeaders;
	}

	protected abstract String pathName();

	protected abstract Object pathObject(T entity);

	protected <P> URI uri(T entity) {
		return fromCurrentContextPath().path(pathName()).buildAndExpand(pathObject(entity)).toUri();
	}
}