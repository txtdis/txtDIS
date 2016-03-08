package ph.txtdis.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import ph.txtdis.domain.ItemFamily;
import ph.txtdis.domain.ItemTree;
import ph.txtdis.repository.ItemTreeRepository;

@RestController("itemTreeController")
@RequestMapping("/itemTrees")
public class ItemTreeController extends IdController<ItemTreeRepository, ItemTree, Long> {

	@RequestMapping(path = "/find", method = RequestMethod.GET)
	public ResponseEntity<?> find(@RequestParam ItemFamily family, @RequestParam ItemFamily parent) {
		ItemTree entity = repository.findByFamilyAndParent(family, parent);
		return new ResponseEntity<>(entity, HttpStatus.OK);
	}

	@RequestMapping(method = RequestMethod.GET)
	public ResponseEntity<?> list() {
		List<ItemTree> list = repository.findByOrderByFamilyAscParentAsc();
		return new ResponseEntity<>(list, HttpStatus.OK);
	}
}