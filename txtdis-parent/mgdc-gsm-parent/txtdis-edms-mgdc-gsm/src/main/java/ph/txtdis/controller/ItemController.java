package ph.txtdis.controller;

import static org.springframework.http.HttpStatus.OK;
import static org.springframework.web.bind.annotation.RequestMethod.GET;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import ph.txtdis.dto.Item;
import ph.txtdis.service.ItemService;

@RequestMapping("/items")
@RestController("itemController")
public class ItemController extends AbstractIdController<ItemService, Item, Long> {

	@RequestMapping(method = GET)
	public ResponseEntity<?> list() {
		List<Item> l = service.list();
		return new ResponseEntity<>(l, OK);
	}
}