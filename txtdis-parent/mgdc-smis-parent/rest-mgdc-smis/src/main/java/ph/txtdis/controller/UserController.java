package ph.txtdis.controller;

import static java.util.stream.Collectors.toList;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.web.bind.annotation.RequestMethod.GET;

import java.util.Arrays;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import ph.txtdis.domain.User;
import ph.txtdis.repository.UserRepository;
import ph.txtdis.type.UserType;

@RestController("userController")
@RequestMapping("/users")
public class UserController extends IdController<UserRepository, User, String> {

	@RequestMapping(path = "/email", method = GET)
	public ResponseEntity<?> findByEmail(@RequestParam("address") String email) {
		User u = repository.findByEmail(email);
		return new ResponseEntity<>(u, OK);
	}

	@RequestMapping(method = GET)
	public ResponseEntity<?> list() {
		List<User> u = repository.findByEnabledTrueOrderByUsernameAsc();
		return new ResponseEntity<>(u, OK);
	}

	@RequestMapping(path = "/role", method = GET)
	public ResponseEntity<?> listNamesByRole(@RequestParam("name") UserType[] types) {
		List<UserType> roles = Arrays.asList(types);
		List<User> users = repository.findByEnabledTrueAndRolesAuthorityInOrderByUsernameAsc(roles);
		users = users.stream().map(u -> nameOnlyUser(u)).collect(toList());
		return new ResponseEntity<>(users, OK);
	}

	private User nameOnlyUser(User u) {
		User user = new User();
		user.setUsername(u.getUsername());
		return user;
	}
}