package ph.txtdis.controller;

import static org.springframework.http.HttpStatus.OK;
import static org.springframework.web.bind.annotation.RequestMethod.GET;

import java.sql.Date;
import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import ph.txtdis.dto.Bom;
import ph.txtdis.service.BomService;

@RequestMapping("/boms")
@RestController("bomController")
public class BomController {

	@Autowired
	private BomService service;

	@RequestMapping(path = "/list", method = GET)
	public ResponseEntity<?> list(@RequestParam("quality") String quality, @RequestParam("direction") String direction,
			@RequestParam("start") Date s, @RequestParam("end") Date e) {
		List<Bom> l = null;
		LocalDate start = s.toLocalDate();
		LocalDate end = e.toLocalDate();
		if (quality.equalsIgnoreCase("good")) {
			if (direction.equalsIgnoreCase("in"))
				l = service.getGoodIncomingList(start, end);
			else
				l = service.getGoodOutgoingList(start, end);
		} else {
			if (direction.equalsIgnoreCase("in"))
				l = service.getBadIncomingList(start, end);
			else
				l = service.getBadOutgoingList(start, end);
		}
		return new ResponseEntity<>(l, OK);
	}
}