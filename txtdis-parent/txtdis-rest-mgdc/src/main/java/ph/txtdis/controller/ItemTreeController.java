package ph.txtdis.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import ph.txtdis.dto.ItemFamily;
import ph.txtdis.dto.ItemTree;
import ph.txtdis.service.ItemTreeService;

@RequestMapping("/itemTrees")
@RestController("itemTreeController")
public class ItemTreeController extends AbstractIdController<ItemTreeService, ItemTree, Long> {

	@RequestMapping(path = "/find", method = RequestMethod.GET)
	public ResponseEntity<?> find(@RequestParam ItemFamily family, @RequestParam ItemFamily parent) {
		ItemTree entity = service.find(family, parent);
		return new ResponseEntity<>(entity, HttpStatus.OK);
	}

	@RequestMapping(method = RequestMethod.GET)
	public ResponseEntity<?> list() {
		List<ItemTree> list = service.list();
		return new ResponseEntity<>(list, HttpStatus.OK);
	}
}