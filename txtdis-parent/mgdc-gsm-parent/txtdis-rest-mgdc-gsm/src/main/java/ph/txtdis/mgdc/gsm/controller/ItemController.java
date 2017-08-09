package ph.txtdis.mgdc.gsm.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ph.txtdis.controller.AbstractSpunSavedKeyedController;
import ph.txtdis.mgdc.gsm.domain.ItemEntity;
import ph.txtdis.mgdc.gsm.dto.Item;
import ph.txtdis.mgdc.gsm.service.server.ItemService;

import java.util.List;

import static org.springframework.http.HttpStatus.OK;
import static org.springframework.web.bind.annotation.RequestMethod.GET;

@RequestMapping("/items")
@RestController("itemController")
public class ItemController //
	extends AbstractSpunSavedKeyedController<ItemService, ItemEntity, Item> {

	@RequestMapping(path = "/find", method = GET)
	public ResponseEntity<?> find(@RequestParam("name") String s) {
		Item i = service.findByName(s);
		return new ResponseEntity<>(i, OK);
	}

	@RequestMapping(path = "/get", method = GET)
	public ResponseEntity<?> get(@RequestParam("vendorId") String s) {
		Item i = service.findByVendorId(s);
		return new ResponseEntity<>(i, OK);
	}

	@RequestMapping(method = GET)
	public ResponseEntity<?> list() {
		List<Item> l = service.list();
		return new ResponseEntity<>(l, OK);
	}

	@RequestMapping(path = "/search", method = GET)
	public ResponseEntity<?> search(@RequestParam("name") String d) {
		List<Item> l = service.searchByDescription(d);
		return new ResponseEntity<>(l, OK);
	}
}