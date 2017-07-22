package ph.txtdis.mgdc.controller;

import static org.springframework.http.HttpStatus.OK;
import static org.springframework.web.bind.annotation.RequestMethod.GET;

import java.sql.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import ph.txtdis.dto.StockTakeVariance;
import ph.txtdis.mgdc.service.server.StockTakeVarianceService;

@RequestMapping("/stockTakeVariances")
@RestController("stockTakeVarianceController")
public class StockTakeVarianceController {

	@Autowired
	private StockTakeVarianceService service;

	@RequestMapping(path = "/list", method = GET)
	public ResponseEntity<?> list(@RequestParam("date") Date d) {
		List<StockTakeVariance> l = service.list(d.toLocalDate());
		return new ResponseEntity<>(l, OK);
	}
}