package ph.txtdis.util;

import static ph.txtdis.util.SpringUtil.setAuthentication;
import static ph.txtdis.util.SpringUtil.setCredentialsForValidation;
import static ph.txtdis.util.SpringUtil.toGranted;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import ph.txtdis.dto.User;
import ph.txtdis.exception.FailedAuthenticationException;
import ph.txtdis.exception.InvalidException;
import ph.txtdis.exception.NoServerConnectionException;
import ph.txtdis.exception.RestException;
import ph.txtdis.exception.StoppedServerException;
import ph.txtdis.service.UserService;

@Component("loginService")
public class LoginService {

	@Autowired
	private UserService service;

	public void validate(String username, String password) throws NoServerConnectionException, StoppedServerException,
			FailedAuthenticationException, InvalidException, RestException {
		setCredentialsForValidation(username, password);
		User user = checkVsDatabase(username);
		setAuthentication(user, password, toGranted(user.getRoles()));
	}

	private User checkVsDatabase(String username) throws NoServerConnectionException, StoppedServerException,
			FailedAuthenticationException, InvalidException, RestException {
		return service.find(username);
	}
}
