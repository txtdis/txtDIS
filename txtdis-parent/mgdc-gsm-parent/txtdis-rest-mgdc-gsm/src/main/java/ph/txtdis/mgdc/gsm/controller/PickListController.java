package ph.txtdis.mgdc.gsm.controller;

import static org.springframework.http.HttpStatus.OK;
import static org.springframework.web.bind.annotation.RequestMethod.GET;

import java.sql.Date;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import ph.txtdis.controller.AbstractSpunSavedKeyedController;
import ph.txtdis.dto.PickList;
import ph.txtdis.exception.NotFoundException;
import ph.txtdis.mgdc.gsm.domain.PickListEntity;
import ph.txtdis.mgdc.gsm.service.server.PickListService;

@RequestMapping("/pickLists")
@RestController("pickListController")
public class PickListController //
		extends AbstractSpunSavedKeyedController<PickListService, PickListEntity, PickList> {

	@RequestMapping(path = "/date", method = GET)
	public ResponseEntity<?> date(@RequestParam("on") Date d) {
		List<PickList> l = service.findAllByDate(d.toLocalDate());
		return new ResponseEntity<>(l, OK);
	}

	@RequestMapping(path = "/find", method = GET)
	public ResponseEntity<?> find(@RequestParam("id") Long id) throws NotFoundException {
		PickList b = service.findByPrimaryKey(id);
		return new ResponseEntity<>(b, OK);
	}

	@RequestMapping(path = "/print", method = GET)
	public ResponseEntity<?> print(@RequestParam("id") Long id) throws Exception {
		PickList l = service.printPickList(id);
		return new ResponseEntity<>(l, OK);
	}
}