package ph.txtdis.controller;

import static java.lang.Long.parseLong;
import static org.springframework.http.HttpStatus.OK;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import ph.txtdis.domain.InvoiceBooklet;
import ph.txtdis.repository.InvoiceBookletRepository;

@RestController("invoiceBookletController")
@RequestMapping("/invoiceBooklets")
public class InvoiceBookletController extends IdController<InvoiceBookletRepository, InvoiceBooklet, Long> {

	@Value("${invoice.line.item.count}")
	private String linesPerPage;

	@RequestMapping(path = "/find", method = RequestMethod.GET)
	public ResponseEntity<?> findById(//
			@RequestParam("prefix") String p, //
			@RequestParam("id") Long id, //
			@RequestParam("suffix") String s) {
		InvoiceBooklet b = repository.findByPrefixAndSuffixAndStartIdLessThanEqualAndEndIdGreaterThanEqual(
				nullIfEmpty(p), nullIfEmpty(s), id, id);
		return new ResponseEntity<>(b, OK);
	}

	@RequestMapping(path = "/linesPerPage", method = RequestMethod.GET)
	public ResponseEntity<?> linesPerPage() {
		InvoiceBooklet b = new InvoiceBooklet();
		b.setEndId(parseLong(linesPerPage));
		return new ResponseEntity<>(b, OK);
	}

	@RequestMapping(method = RequestMethod.GET)
	public ResponseEntity<?> list() {
		List<InvoiceBooklet> list = repository.findByOrderByPrefixAscStartIdAscSuffixAsc();
		return new ResponseEntity<>(list, OK);
	}

	private String nullIfEmpty(String s) {
		return s.isEmpty() ? null : s;
	}
}