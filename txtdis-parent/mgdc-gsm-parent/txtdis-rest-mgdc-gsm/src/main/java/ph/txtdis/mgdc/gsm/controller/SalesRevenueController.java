package ph.txtdis.mgdc.gsm.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ph.txtdis.dto.SalesRevenue;
import ph.txtdis.exception.DateBeforeGoLiveException;
import ph.txtdis.exception.EndDateBeforeStartException;
import ph.txtdis.mgdc.service.server.SalesRevenueService;

import java.sql.Date;
import java.util.List;

import static org.springframework.http.HttpStatus.OK;
import static org.springframework.web.bind.annotation.RequestMethod.GET;

@RequestMapping("/salesRevenues")
@RestController("salesRevenueController")
public class SalesRevenueController {

	@Autowired
	private SalesRevenueService service;

	@RequestMapping(path = "/list", method = GET)
	public ResponseEntity<?> list(@RequestParam("start") Date s, @RequestParam("end") Date e)
		throws DateBeforeGoLiveException, EndDateBeforeStartException {
		List<SalesRevenue> l = service.list(s.toLocalDate(), e.toLocalDate());
		return new ResponseEntity<>(l, OK);
	}
}