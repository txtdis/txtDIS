package ph.txtdis.controller;

import static org.springframework.http.HttpStatus.OK;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import ph.txtdis.dto.InvoiceBooklet;
import ph.txtdis.service.InvoiceBookletService;

@RestController("invoiceBookletController")
@RequestMapping("/invoiceBooklets")
public class InvoiceBookletController extends AbstractIdController<InvoiceBookletService, InvoiceBooklet, Long> {

	@RequestMapping(path = "/find", method = RequestMethod.GET)
	public ResponseEntity<?> findById(//
			@RequestParam("prefix") String p, //
			@RequestParam("id") Long id, //
			@RequestParam("suffix") String s) {
		InvoiceBooklet b = service.findById(p, id, s);
		return new ResponseEntity<>(b, OK);
	}

	@RequestMapping(path = "/linesPerPage", method = RequestMethod.GET)
	public ResponseEntity<?> linesPerPage() {
		InvoiceBooklet b = service.linesPerPage();
		return new ResponseEntity<>(b, OK);
	}

	@RequestMapping(method = RequestMethod.GET)
	public ResponseEntity<?> list() {
		List<InvoiceBooklet> list = service.list();
		return new ResponseEntity<>(list, OK);
	}
}