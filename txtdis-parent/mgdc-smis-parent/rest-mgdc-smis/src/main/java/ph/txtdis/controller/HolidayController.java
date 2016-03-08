package ph.txtdis.controller;

import static org.springframework.http.HttpStatus.OK;
import static org.springframework.web.bind.annotation.RequestMethod.GET;

import java.sql.Date;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import ph.txtdis.domain.Holiday;
import ph.txtdis.repository.HolidayRepository;

@RequestMapping("/holidays")
@RestController("holidayController")
public class HolidayController extends IdController<HolidayRepository, Holiday, Long> {

	@RequestMapping(path = "/find", method = GET)
	public ResponseEntity<?> findByDate(@RequestParam("date") Date d) {
		Holiday h = repository.findByDeclaredDate(d.toLocalDate());
		return new ResponseEntity<>(h, OK);
	}

	@RequestMapping(method = GET)
	public ResponseEntity<?> list() {
		List<Holiday> list = repository.findByOrderByDeclaredDateDesc();
		return new ResponseEntity<>(list, OK);
	}
}