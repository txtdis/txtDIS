package ph.txtdis.controller;

import static org.springframework.http.HttpStatus.OK;
import static org.springframework.web.bind.annotation.RequestMethod.GET;

import java.sql.Date;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import ph.txtdis.dto.PickList;
import ph.txtdis.service.PickListService;

@RequestMapping("/pickLists")
@RestController("pickListController")
public class PickListController extends AbstractSpunController<PickListService, PickList, Long> {

	@RequestMapping(path = "/date", method = GET)
	public ResponseEntity<?> findByDate(@RequestParam("on") Date d) {
		List<PickList> l = service.findByDate(d);
		return new ResponseEntity<>(l, OK);
	}

	@RequestMapping(path = "/print", method = GET)
	public ResponseEntity<?> printPickList(@RequestParam("id") Long id) throws Exception {
		PickList l = service.printPickList(id);
		return new ResponseEntity<>(l, OK);
	}
}