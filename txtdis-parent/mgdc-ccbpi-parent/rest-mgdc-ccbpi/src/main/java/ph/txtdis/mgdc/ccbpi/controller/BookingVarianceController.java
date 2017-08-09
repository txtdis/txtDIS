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
import ph.txtdis.mgdc.ccbpi.service.server.BookingVarianceService;

@RequestMapping("/bookingVariances")
@RestController("bookingVarianceController")
public class BookingVarianceController {

	private static Logger logger = getLogger(BookingVarianceController.class);

	@Autowired
	private BookingVarianceService service;

	@RequestMapping(path = "/list", method = GET)
	public ResponseEntity<?> list( //
	                               @RequestParam("route") String r, //
	                               @RequestParam("start") Date s, //
	                               @RequestParam("end") Date e) {
		logger.info("\n    Route = " + r);
		List<SalesItemVariance> l = service.list(r, s.toLocalDate(), e.toLocalDate());
		return new ResponseEntity<>(l, OK);
	}

	@RequestMapping(path = "/ddlList", method = GET)
	public ResponseEntity<?> listDDL( //
	                                  @RequestParam("itemVendorNo") String i, //
	                                  @RequestParam("route") String r, //
	                                  @RequestParam("start") Date s, //
	                                  @RequestParam("end") Date e) {
		List<SalesItemVariance> l = service.listDDL(i, r, s.toLocalDate(), e.toLocalDate());
		return new ResponseEntity<>(l, OK);
	}

	@RequestMapping(path = "/ocsList", method = GET)
	public ResponseEntity<?> listOCS( //
	                                  @RequestParam("itemVendorNo") String i, //
	                                  @RequestParam("route") String r, //
	                                  @RequestParam("start") Date s, //
	                                  @RequestParam("end") Date e) {
		List<SalesItemVariance> l = service.listOCS(i, r, s.toLocalDate(), e.toLocalDate());
		return new ResponseEntity<>(l, OK);
	}

	@RequestMapping(path = "/rrList", method = GET)
	public ResponseEntity<?> listRR( //
	                                 @RequestParam("itemVendorNo") String i, //
	                                 @RequestParam("route") String r, //
	                                 @RequestParam("start") Date s, //
	                                 @RequestParam("end") Date e) {
		List<SalesItemVariance> l = service.listRR(i, r, s.toLocalDate(), e.toLocalDate());
		return new ResponseEntity<>(l, OK);
	}
}