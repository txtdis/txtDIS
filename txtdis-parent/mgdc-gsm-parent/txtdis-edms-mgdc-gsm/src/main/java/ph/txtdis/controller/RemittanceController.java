package ph.txtdis.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ph.txtdis.dto.Remittance;
import ph.txtdis.service.EdmsRemittanceService;

import java.util.List;

import static org.springframework.http.HttpStatus.OK;
import static org.springframework.web.bind.annotation.RequestMethod.GET;

@RequestMapping("/remittances")
@RestController("remittanceController")
public class RemittanceController {

	@Autowired
	private EdmsRemittanceService service;

	@RequestMapping(method = GET)
	public ResponseEntity<?> list() {
		List<Remittance> l = service.list();
		return new ResponseEntity<>(l, OK);
	}
}