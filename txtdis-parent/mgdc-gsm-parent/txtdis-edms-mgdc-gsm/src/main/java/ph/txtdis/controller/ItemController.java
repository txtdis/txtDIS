package ph.txtdis.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ph.txtdis.mgdc.gsm.dto.Item;
import ph.txtdis.service.EdmsItemService;

import java.util.List;

import static org.springframework.http.HttpStatus.OK;
import static org.springframework.web.bind.annotation.RequestMethod.GET;

@RequestMapping("/items")
@RestController("itemController")
public class ItemController //
	extends AbstractSavedController<EdmsItemService, Item, Long> {

	@RequestMapping(method = GET)
	public ResponseEntity<?> list() {
		List<Item> l = service.list();
		return new ResponseEntity<>(l, OK);
	}
}