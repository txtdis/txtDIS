package ph.txtdis.controller;

import static org.springframework.http.HttpStatus.OK;
import static org.springframework.web.bind.annotation.RequestMethod.GET;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import ph.txtdis.domain.Bank;
import ph.txtdis.repository.BankRepository;

@RequestMapping("/banks")
@RestController("bankController")
public class BankController extends NameListController<BankRepository, Bank> {

	@Override
	@RequestMapping(method = GET)
	public ResponseEntity<?> list() {
		List<Bank> l = (List<Bank>) repository.findAll();
		return new ResponseEntity<>(l, OK);
	}
}