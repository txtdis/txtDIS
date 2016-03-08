package ph.txtdis.controller;

import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import ph.txtdis.domain.Script;
import ph.txtdis.repository.ScriptRepository;

@RequestMapping("/scripts")
@RestController("scriptController")
public class ScriptController extends IdController<ScriptRepository, Script, Long> {

	@RequestMapping(path = "/unposted", method = GET)
	public ResponseEntity<?> getUnpostedTransactions() {
		Script s = repository.findFirstBySentFalse();
		return new ResponseEntity<>(s, OK);
	}

	@RequestMapping(path = "/all", method = POST)
	public ResponseEntity<?> save(@RequestBody List<Script> s) {
		s = (List<Script>) repository.save(s);
		return new ResponseEntity<>(s, CREATED);
	}
}