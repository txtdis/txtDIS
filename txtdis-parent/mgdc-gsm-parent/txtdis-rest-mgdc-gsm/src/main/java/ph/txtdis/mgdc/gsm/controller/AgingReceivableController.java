package ph.txtdis.mgdc.gsm.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ph.txtdis.dto.AgingReceivableReport;
import ph.txtdis.mgdc.gsm.service.server.AgingReceivableService;

import static org.springframework.web.bind.annotation.RequestMethod.GET;

@RequestMapping("/agingReceivables")
@RestController("agingReceivableController")
public class AgingReceivableController {

	@Autowired
	private AgingReceivableService service;

	@RequestMapping(method = GET)
	public ResponseEntity<?> agingReceivableReport() {
		AgingReceivableReport arr = service.getAgingReceivableReport();
		return new ResponseEntity<>(arr, HttpStatus.OK);
	}
}