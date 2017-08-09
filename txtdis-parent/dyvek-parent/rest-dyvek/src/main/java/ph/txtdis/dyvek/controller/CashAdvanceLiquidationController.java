package ph.txtdis.dyvek.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ph.txtdis.dto.RemittanceDetail;
import ph.txtdis.dyvek.service.server.CashAdvanceService;
import ph.txtdis.dyvek.service.server.DyvekRemittanceService;

import java.util.List;

import static org.springframework.http.HttpStatus.OK;
import static org.springframework.web.bind.annotation.RequestMethod.GET;

@RequestMapping("/cashAdvanceLiquidations")
@RestController("cashAdvanceLiquidationController")
public class CashAdvanceLiquidationController {

	@Autowired
	private DyvekRemittanceService service;

	@RequestMapping(path = "/find", method = GET)
	public ResponseEntity<List<RemittanceDetail>> find(@RequestParam("id") Long id) {
		List<RemittanceDetail> list = service.findLiquidationsByCashAdvanceId(id);
		return new ResponseEntity<>(list, OK);
	}
}