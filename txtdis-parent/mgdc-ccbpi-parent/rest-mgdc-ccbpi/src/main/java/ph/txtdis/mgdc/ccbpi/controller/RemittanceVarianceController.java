package ph.txtdis.mgdc.ccbpi.controller;

import static org.apache.log4j.Logger.getLogger;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.web.bind.annotation.RequestMethod.GET;

import java.sql.Date;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import ph.txtdis.dto.SalesItemVariance;
import ph.txtdis.mgdc.ccbpi.service.server.RemittanceVarianceService;

@RequestMapping("/remittanceVariances")
@RestController("remittanceVarianceController")
public class RemittanceVarianceController {

	private static Logger logger = getLogger(RemittanceVarianceController.class);

	@Autowired
	private RemittanceVarianceService service;

	@RequestMapping(path = "/list", method = GET)
	public ResponseEntity<?> list( //
			@RequestParam("collector") String c, //
			@RequestParam("start") Date s, //
			@RequestParam("end") Date e) {
		logger.info("\n    Collector = " + c);
		List<SalesItemVariance> l = service.list(c, s.toLocalDate(), e.toLocalDate());
		return new ResponseEntity<>(l, OK);
	}

	@RequestMapping(path = "/deliveredList", method = GET)
	public ResponseEntity<?> deliveredList( //
			@RequestParam("route") String r, //
			@RequestParam("start") Date s, //
			@RequestParam("end") Date e) {
		List<SalesItemVariance> l = service.listDelivered(r, s.toLocalDate(), e.toLocalDate());
		return new ResponseEntity<>(l, OK);
	}

	@RequestMapping(path = "/loadedList", method = GET)
	public ResponseEntity<?> loadedList( //
			@RequestParam("route") String r, //
			@RequestParam("start") Date s, //
			@RequestParam("end") Date e) {
		List<SalesItemVariance> l = service.listLoaded(r, s.toLocalDate(), e.toLocalDate());
		return new ResponseEntity<>(l, OK);
	}

	@RequestMapping(path = "/unpickedList", method = GET)
	public ResponseEntity<?> listUnpicked( //
			@RequestParam("route") String r, //
			@RequestParam("start") Date s, //
			@RequestParam("end") Date e) {
		List<SalesItemVariance> l = service.listUnpicked(r, s.toLocalDate(), e.toLocalDate());
		return new ResponseEntity<>(l, OK);
	}
}