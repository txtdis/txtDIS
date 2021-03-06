package ph.txtdis.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ph.txtdis.domain.UserEntity;
import ph.txtdis.dto.User;
import ph.txtdis.service.ServerUserService;
import ph.txtdis.type.UserType;

import java.util.Arrays;
import java.util.List;

import static org.springframework.http.HttpStatus.OK;
import static org.springframework.web.bind.annotation.RequestMethod.GET;

@RestController("userController")
@RequestMapping("/users")
public class UserController //
	extends AbstractSavedKeyedController<ServerUserService, UserEntity, User, String> {

	@RequestMapping(path = "/email", method = GET)
	public ResponseEntity<?> findByEmail(@RequestParam("address") String email) {
		User u = service.findByEmail(email);
		return new ResponseEntity<>(u, OK);
	}

	@RequestMapping(method = GET)
	public ResponseEntity<?> list() {
		List<User> u = service.list();
		return new ResponseEntity<>(u, OK);
	}

	@RequestMapping(path = "/role", method = GET)
	public ResponseEntity<?> listNamesByRole(@RequestParam("name") UserType[] types) {
		List<User> users = service.listNamesByRole(Arrays.asList(types));
		return new ResponseEntity<>(users, OK);
	}
}