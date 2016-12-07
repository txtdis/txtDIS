package ph.txtdis.controller;

import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.web.bind.annotation.RequestMethod.POST;
import static org.springframework.web.servlet.support.ServletUriComponentsBuilder.fromCurrentContextPath;

import java.io.Serializable;
import java.net.URI;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import ph.txtdis.dto.Keyed;
import ph.txtdis.service.CreateService;

public abstract class AbstractCreateController< //
		S extends CreateService<T, PK>, //
		T extends Keyed<PK>, //
		PK extends Serializable> {

	@Autowired
	protected S service;

	@RequestMapping(method = POST)
	public ResponseEntity<?> save(@RequestBody T body) {
		body = service.save(body);
		return new ResponseEntity<>(body, httpHeaders(body), CREATED);
	}

	protected MultiValueMap<String, String> httpHeaders(T body) {
		URI uri = fromCurrentContextPath().path(pathName()).buildAndExpand(pathObject(body)).toUri();
		HttpHeaders httpHeaders = new HttpHeaders();
		httpHeaders.setLocation(uri);
		return httpHeaders;
	}

	protected abstract String pathName();

	protected abstract Object pathObject(T body);
}