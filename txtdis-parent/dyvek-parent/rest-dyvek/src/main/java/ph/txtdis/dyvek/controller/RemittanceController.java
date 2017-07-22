package ph.txtdis.dyvek.controller;

import static org.springframework.http.HttpStatus.OK;
import static org.springframework.web.bind.annotation.RequestMethod.GET;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import ph.txtdis.controller.AbstractSpunSavedKeyedController;
import ph.txtdis.dto.Remittance;
import ph.txtdis.dyvek.domain.RemittanceEntity;
import ph.txtdis.dyvek.model.Billable;
import ph.txtdis.dyvek.service.server.DyvekRemittanceService;
import ph.txtdis.exception.NotFoundException;

@RequestMapping("/remittances")
@RestController("remittanceController")
public class RemittanceController //
		extends AbstractSpunSavedKeyedController<DyvekRemittanceService, RemittanceEntity, Remittance> {

	@Value("${go.live}")
	private String goLive;

	@RequestMapping(path = "/billings", method = GET)
	public ResponseEntity<?> billings(@RequestParam("id") Billable b) {
		List<Remittance> r = service.findAll(b);
		return new ResponseEntity<>(r, OK);
	}

	@RequestMapping(path = "/check", method = GET)
	public ResponseEntity<?> check( //
			@RequestParam("bank") String bank, //
			@RequestParam("id") Long id) {
		Remittance r = service.findByCheck(bank, id);
		return new ResponseEntity<>(r, OK);
	}

	@RequestMapping(path = "/find", method = GET)
	public ResponseEntity<?> find(@RequestParam("id") Long id) throws NotFoundException {
		Remittance r = service.findByPrimaryKey(id);
		return new ResponseEntity<>(r, OK);
	}
}