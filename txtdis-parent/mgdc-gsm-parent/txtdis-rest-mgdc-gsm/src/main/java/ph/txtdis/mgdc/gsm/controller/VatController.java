package ph.txtdis.mgdc.gsm.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ph.txtdis.mgdc.gsm.dto.Vat;
import ph.txtdis.mgdc.gsm.service.server.VatService;

import java.sql.Date;
import java.util.List;

import static org.springframework.http.HttpStatus.OK;

@RequestMapping("/vats")
@RestController("vatController")
public class VatController {

	@Autowired
	private VatService service;

	@RequestMapping(path = "/rate", method = RequestMethod.GET)
	public ResponseEntity<?> getVat() {
		Vat v = service.getVat();
		return new ResponseEntity<>(v, OK);
	}

	@RequestMapping(path = "/list", method = RequestMethod.GET)
	public ResponseEntity<?> list(@RequestParam("start") Date s, @RequestParam("end") Date e) {
		List<Vat> l = service.list(s.toLocalDate(), e.toLocalDate());
		return new ResponseEntity<>(l, OK);
	}
}