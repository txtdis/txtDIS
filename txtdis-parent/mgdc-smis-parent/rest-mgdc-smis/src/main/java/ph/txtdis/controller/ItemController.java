package ph.txtdis.controller;

import static java.util.stream.Collectors.toList;
import static org.springframework.http.HttpStatus.NO_CONTENT;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.web.bind.annotation.RequestMethod.DELETE;
import static org.springframework.web.bind.annotation.RequestMethod.GET;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import ph.txtdis.domain.Item;
import ph.txtdis.repository.ItemRepository;

@RestController("itemController")
@RequestMapping("/items")
public class ItemController extends SpunController<ItemRepository, Item, Long> {

	@RequestMapping(method = DELETE)
	public ResponseEntity<?> delete(@PathVariable Long id) {
		repository.delete(id);
		return new ResponseEntity<>(NO_CONTENT);
	}

	@RequestMapping(path = "/find", method = GET)
	public ResponseEntity<?> findByName(@RequestParam("name") String s) {
		Item i = repository.findByName(s);
		return new ResponseEntity<>(i, OK);
	}

	@RequestMapping(method = GET)
	public ResponseEntity<?> list() {
		List<Item> l = (List<Item>) repository.findAll();
		if (l != null)
			l = l.stream().map(i -> idAndNameOnly(i)).collect(toList());
		return new ResponseEntity<>(l, OK);
	}

	@RequestMapping(path = "/search", method = GET)
	public ResponseEntity<?> searchByDescription(@RequestParam("name") String s) {
		List<Item> l = repository.findByDescriptionContaining(s);
		if (l != null)
			l = l.stream().map(i -> idAndNameOnly(i)).collect(toList());
		return new ResponseEntity<>(l, OK);
	}

	private Item idAndNameOnly(Item i) {
		Item n = new Item();
		n.setId(i.getId());
		n.setName(i.getName());
		n.setDescription(i.getDescription());
		return n;
	}
}