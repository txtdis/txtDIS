package ph.txtdis.controller;

import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;
import static org.springframework.web.servlet.support.ServletUriComponentsBuilder.fromCurrentContextPath;

import java.net.URI;
import java.util.List;

import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import ph.txtdis.dto.Item;
import ph.txtdis.service.ItemService;

public abstract class AbstractItemController<AS extends ItemService> extends AbstractSpunController<AS, Item, Long> {

	@RequestMapping(path = "/find", method = GET)
	public ResponseEntity<?> findByName(@RequestParam("name") String s) {
		Item i = service.findByName(s);
		return new ResponseEntity<>(i, OK);
	}

	@RequestMapping(path = "/get", method = GET)
	public ResponseEntity<?> findByVendorId(@RequestParam("vendorId") String s) {
		Item i = service.findByVendorId(s);
		return new ResponseEntity<>(i, OK);
	}

	@RequestMapping(method = GET)
	public ResponseEntity<?> list() {
		List<Item> l = service.list();
		return new ResponseEntity<>(l, OK);
	}

	@Override
	@RequestMapping(method = POST)
	public ResponseEntity<?> save(@RequestBody Item body) {
		body = service.save(body);
		return new ResponseEntity<>(body, httpHeaders(body), CREATED);
	}

	@Override
	protected MultiValueMap<String, String> httpHeaders(Item body) {
		URI uri = fromCurrentContextPath().path("/{id}").buildAndExpand(body.getId()).toUri();
		HttpHeaders httpHeaders = new HttpHeaders();
		httpHeaders.setLocation(uri);
		return httpHeaders;
	}

	@RequestMapping(path = "/search", method = GET)
	public ResponseEntity<?> searchByDescription(@RequestParam("name") String d) {
		List<Item> l = service.searchByDescription(d);
		return new ResponseEntity<>(l, OK);
	}
}