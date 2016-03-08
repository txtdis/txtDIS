package ph.txtdis.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import ph.txtdis.domain.User;
import ph.txtdis.repository.UserRepository;

@RestController("styleController")
@RequestMapping("/styles")
public class StyleController extends IdController<UserRepository, User, String> {

	@RequestMapping(path = "/find", method = RequestMethod.GET)
	public ResponseEntity<?> findByEmail(@RequestParam("email") String email) {
		User user = repository.findByEmail(email);
		return new ResponseEntity<>(user, HttpStatus.OK);
	}
}