package ph.txtdis.mgdc.ccbpi.controller;

import static org.springframework.http.HttpStatus.OK;
import static org.springframework.web.bind.annotation.RequestMethod.GET;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import ph.txtdis.dto.SalesItemVariance;
import ph.txtdis.mgdc.ccbpi.service.server.BlanketBalanceService;

@RequestMapping("/blanketBalances")
@RestController("blanketBalanceController")
public class BlanketBalanceController {

	@Autowired
	private BlanketBalanceService service;

	@RequestMapping(method = GET)
	public ResponseEntity<?> list() {
		List<SalesItemVariance> l = service.list();
		return new ResponseEntity<>(l, OK);
	}
}