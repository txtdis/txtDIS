package ph.txtdis.mgdc.gsm.controller;

import static org.springframework.http.HttpStatus.OK;
import static org.springframework.web.bind.annotation.RequestMethod.GET;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import ph.txtdis.controller.AbstractSpunSavedKeyedController;
import ph.txtdis.dto.CreditNote;
import ph.txtdis.mgdc.gsm.domain.CreditNoteEntity;
import ph.txtdis.mgdc.gsm.service.server.CreditNoteService;

@RequestMapping("/creditNotes")
@RestController("creditNoteController")
public class CreditNoteController //
		extends AbstractSpunSavedKeyedController<CreditNoteService, CreditNoteEntity, CreditNote> {

	@RequestMapping(method = GET)
	public ResponseEntity<?> list() {
		List<CreditNote> l = service.findAll();
		return new ResponseEntity<>(l, OK);
	}

	@RequestMapping(path = "/unpaid", method = GET)
	public ResponseEntity<?> unpaid() {
		List<CreditNote> l = service.findAllUnpaid();
		return new ResponseEntity<>(l, OK);
	}

	@RequestMapping(path = "/unvalidated", method = GET)
	public ResponseEntity<?> unvalidated() {
		List<CreditNote> l = service.findAllUnvalidated();
		return new ResponseEntity<>(l, OK);
	}
}