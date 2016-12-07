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

import ph.txtdis.dto.Script;
import ph.txtdis.service.ScriptService;

@RequestMapping("/scripts")
@RestController("scriptController")
public class ScriptController extends AbstractIdController<ScriptService, Script, Long> {

	@RequestMapping(path = "/unposted", method = GET)
	public ResponseEntity<?> getUnpostedTransactions() {
		Script s = service.getUnpostedTransactions();
		return new ResponseEntity<>(s, OK);
	}

	@RequestMapping(path = "/all", method = POST)
	public ResponseEntity<?> save(@RequestBody List<Script> s) {
		s = service.save(s);
		return new ResponseEntity<>(s, CREATED);
	}
}