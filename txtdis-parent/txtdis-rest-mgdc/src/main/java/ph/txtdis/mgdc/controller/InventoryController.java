package ph.txtdis.mgdc.controller;

import static org.springframework.http.HttpStatus.OK;
import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import ph.txtdis.dto.Inventory;
import ph.txtdis.dto.Stock;
import ph.txtdis.mgdc.service.server.InventoryService;

@RequestMapping("/inventories")
@RestController("inventoryController")
public class InventoryController {

	@Autowired
	private InventoryService service;

	@RequestMapping(method = GET)
	public ResponseEntity<?> list() {
		List<Inventory> i = service.list();
		return new ResponseEntity<>(i, OK);
	}

	@RequestMapping(path = "/item", method = GET)
	public ResponseEntity<?> item(@RequestParam("id") Long id) {
		Inventory i = service.next(id);
		return new ResponseEntity<>(i, OK);
	}

	@RequestMapping(path = "/update", method = POST)
	public ResponseEntity<?> update() {
		Iterable<Stock> is = service.update();
		return new ResponseEntity<>(is, OK);
	}
}