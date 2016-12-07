package ph.txtdis.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ph.txtdis.dto.User;

@Service("loginService")
public class LoginServiceImpl implements LoginService {

	@Autowired
	private CredentialService credentialService;

	@Autowired
	private UserService userService;

	@Override
	public void validate(String username, String password) throws Exception {
		credentialService.setCredentialsForValidation(username, password);
		User user = checkVsDatabase(username);
		credentialService.setAuthentication(user, password, user.getRoles());
	}

	private User checkVsDatabase(String username) throws Exception {
		return userService.find(username);
	}
}
