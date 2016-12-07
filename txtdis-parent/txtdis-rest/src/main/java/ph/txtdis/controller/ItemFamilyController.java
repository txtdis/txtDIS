package ph.txtdis.controller;

import static org.springframework.http.HttpStatus.OK;
import static org.springframework.web.bind.annotation.RequestMethod.GET;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import ph.txtdis.dto.ItemFamily;
import ph.txtdis.service.ItemFamilyService;

@RequestMapping("/itemFamilies")
@RestController("itemFamilyController")
public class ItemFamilyController extends AbstractNameListController<ItemFamilyService, ItemFamily> {

	@Override
	@RequestMapping(path = "/ancestry", method = GET)
	public ResponseEntity<?> find(@RequestParam("family") String f) {
		List<ItemFamily> l = service.listAncestry(f);
		return new ResponseEntity<>(l, OK);
	}

	@RequestMapping(path = "/parents", method = GET)
	public ResponseEntity<?> findParents() {
		List<ItemFamily> l = service.findParents();
		return new ResponseEntity<>(l, OK);
	}

	@RequestMapping(path = "/brands", method = RequestMethod.GET)
	public ResponseEntity<?> findBrands(@RequestParam("of") String category) {
		List<ItemFamily> list = service.findBrands(category);
		return new ResponseEntity<>(list, HttpStatus.OK);
	}

	@RequestMapping(path = "/categories", method = RequestMethod.GET)
	public ResponseEntity<?> findCategories(@RequestParam("of") String clazz) {
		List<ItemFamily> list = service.findCategories(clazz);
		return new ResponseEntity<>(list, HttpStatus.OK);
	}

	@RequestMapping(path = "/classes", method = RequestMethod.GET)
	public ResponseEntity<?> findClasses() {
		List<ItemFamily> list = service.findClasses();
		return new ResponseEntity<>(list, HttpStatus.OK);
	}
}