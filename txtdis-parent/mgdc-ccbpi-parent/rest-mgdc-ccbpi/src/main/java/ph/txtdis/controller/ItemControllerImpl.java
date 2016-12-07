package ph.txtdis.controller;

import static org.springframework.http.HttpStatus.OK;
import static org.springframework.web.bind.annotation.RequestMethod.GET;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import ph.txtdis.dto.Item;
import ph.txtdis.service.EmptiesItemService;

@RequestMapping("/items")
@RestController("itemController")
public class ItemControllerImpl extends AbstractItemController<EmptiesItemService> {

	@RequestMapping(path = "/empties", method = GET)
	public ResponseEntity<?> findEmpties() {
		List<Item> l = service.findEmpties();
		return new ResponseEntity<>(l, OK);
	}
}