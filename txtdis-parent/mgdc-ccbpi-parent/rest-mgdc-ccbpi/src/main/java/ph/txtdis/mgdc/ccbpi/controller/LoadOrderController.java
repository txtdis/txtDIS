package ph.txtdis.mgdc.ccbpi.controller;

import static org.springframework.http.HttpStatus.OK;
import static org.springframework.web.bind.annotation.RequestMethod.GET;

import java.sql.Date;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import ph.txtdis.dto.Billable;

@RequestMapping("/loadOrders")
@RestController("loadOrderController")
public class LoadOrderController {

	@RequestMapping(path = "/open", method = GET)
	public ResponseEntity<?> open(@RequestParam("seller") String s, @RequestParam("upTo") Date d) {
		Billable b = null; //service.findOpenLoadOrder(s, d.toLocalDate());
		return new ResponseEntity<>(b, OK);
	}

	@RequestMapping(path = "/short", method = GET)
	public ResponseEntity<?> shortage(@RequestParam("id") Long id) {
		Billable b = null; //service.findShortLoadOrder(id);
		return new ResponseEntity<>(b, OK);
	}
}