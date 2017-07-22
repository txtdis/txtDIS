package ph.txtdis.mgdc.controller;

import static org.springframework.http.HttpStatus.OK;
import static org.springframework.web.bind.annotation.RequestMethod.GET;

import java.sql.Date;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import ph.txtdis.controller.AbstractSavedController;
import ph.txtdis.dto.Holiday;
import ph.txtdis.mgdc.service.server.HolidayService;

@RequestMapping("/holidays")
@RestController("holidayController")
public class HolidayController //
		extends AbstractSavedController<HolidayService, Holiday, Long> {

	@RequestMapping(path = "/find", method = GET)
	public ResponseEntity<?> find(@RequestParam("date") Date d) {
		Holiday h = service.findByDate(d.toLocalDate());
		return new ResponseEntity<>(h, OK);
	}

	@RequestMapping(method = GET)
	public ResponseEntity<?> list() {
		List<Holiday> list = service.list();
		return new ResponseEntity<>(list, OK);
	}
}