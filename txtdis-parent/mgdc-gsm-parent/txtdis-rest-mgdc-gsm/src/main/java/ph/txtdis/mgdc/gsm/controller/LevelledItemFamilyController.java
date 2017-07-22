package ph.txtdis.mgdc.gsm.controller;

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
import ph.txtdis.mgdc.controller.AbstractItemFamilyController;
import ph.txtdis.mgdc.gsm.service.server.ImportedLeveledItemFamilyService;

@RequestMapping("/itemFamilies")
@RestController("itemFamilyController")
public class LevelledItemFamilyController //
		extends AbstractItemFamilyController<ImportedLeveledItemFamilyService> {

	@RequestMapping(path = "/ancestry", method = GET)
	public ResponseEntity<?> ancestry(@RequestParam("family") String f) {
		List<ItemFamily> l = service.listAncestry(f);
		return new ResponseEntity<>(l, OK);
	}

	@RequestMapping(path = "/parents", method = GET)
	public ResponseEntity<?> parents() {
		List<ItemFamily> l = service.findParents();
		return new ResponseEntity<>(l, OK);
	}

	@RequestMapping(path = "/brands", method = RequestMethod.GET)
	public ResponseEntity<?> brands(@RequestParam("of") String category) {
		List<ItemFamily> list = service.findBrands(category);
		return new ResponseEntity<>(list, HttpStatus.OK);
	}

	@RequestMapping(path = "/categories", method = RequestMethod.GET)
	public ResponseEntity<?> categories(@RequestParam("of") String clazz) {
		List<ItemFamily> list = service.findCategories(clazz);
		return new ResponseEntity<>(list, HttpStatus.OK);
	}

	@RequestMapping(path = "/classes", method = RequestMethod.GET)
	public ResponseEntity<?> classes() {
		List<ItemFamily> list = service.findClasses();
		return new ResponseEntity<>(list, HttpStatus.OK);
	}
}