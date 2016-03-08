package ph.txtdis.controller;

import static org.springframework.http.HttpStatus.OK;
import static org.springframework.web.bind.annotation.RequestMethod.GET;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import ph.txtdis.domain.Item;
import ph.txtdis.repository.ItemRepository;

@RequestMapping("/items")
@RestController("itemController")
public class ItemController extends NameListController<ItemRepository, Item> {

	@Override
	@RequestMapping(method = GET)
	public ResponseEntity<?> list() {
		List<Item> l = (List<Item>) repository.findAll();
		return new ResponseEntity<>(l, OK);
	}
}