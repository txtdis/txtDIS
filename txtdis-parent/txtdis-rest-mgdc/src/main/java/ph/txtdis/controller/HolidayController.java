package ph.txtdis.controller;

import static org.springframework.http.HttpStatus.OK;
import static org.springframework.web.bind.annotation.RequestMethod.GET;

import java.sql.Date;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import ph.txtdis.dto.Holiday;
import ph.txtdis.service.HolidayService;

@RequestMapping("/holidays")
@RestController("holidayController")
public class HolidayController extends AbstractIdController<HolidayService, Holiday, Long> {

	@RequestMapping(path = "/find", method = GET)
	public ResponseEntity<?> findByDate(@RequestParam("date") Date d) {
		Holiday h = service.findByDate(d);
		return new ResponseEntity<>(h, OK);
	}

	@RequestMapping(method = GET)
	public ResponseEntity<?> list() {
		List<Holiday> list = service.list();
		return new ResponseEntity<>(list, OK);
	}
}