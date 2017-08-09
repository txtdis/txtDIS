package ph.txtdis.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ph.txtdis.dto.User;

import static ph.txtdis.util.UserUtils.setAuthentication;
import static ph.txtdis.util.UserUtils.setCredentialsForValidation;

@Service("loginService")
public class LoginServiceImpl
	implements LoginService {

	@Autowired
	private UserService userService;

	@Override
	public void validate(String username, String password) throws Exception {
		setCredentialsForValidation(username, password);
		User user = checkVsDatabase(username);
		setAuthentication(user, password, user.getRoles());
	}

	private User checkVsDatabase(String username) throws Exception {
		return userService.find(username);
	}
}
