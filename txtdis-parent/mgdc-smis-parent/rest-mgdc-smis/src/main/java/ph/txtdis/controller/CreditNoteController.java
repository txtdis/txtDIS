package ph.txtdis.controller;

import static org.springframework.http.HttpStatus.OK;
import static org.springframework.web.bind.annotation.RequestMethod.GET;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import ph.txtdis.domain.CreditNote;
import ph.txtdis.repository.CreditNoteRepository;

@RequestMapping("/creditNotes")
@RestController("creditNoteController")
public class CreditNoteController extends SpunController<CreditNoteRepository, CreditNote, Long> {

	@RequestMapping(method = GET)
	public ResponseEntity<?> listCreditNotes() {
		List<CreditNote> l = repository.findByOrderByIdAsc();
		return new ResponseEntity<>(l, OK);
	}
}