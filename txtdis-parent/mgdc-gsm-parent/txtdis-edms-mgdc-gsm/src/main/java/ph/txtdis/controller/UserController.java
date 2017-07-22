package ph.txtdis.controller;

import static org.springframework.http.HttpStatus.OK;
import static org.springframework.web.bind.annotation.RequestMethod.GET;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import ph.txtdis.dto.User;
import ph.txtdis.service.EdmsUserService;

@RequestMapping("/users")
@RestController("userController")
public class UserController {

	@Autowired
	private EdmsUserService service;

	@RequestMapping(method = GET)
	public ResponseEntity<?> list() {
		List<User> u = service.list();
		return new ResponseEntity<>(u, OK);
	}
}