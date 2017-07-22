package ph.txtdis.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Component;

import ph.txtdis.dto.User;
import ph.txtdis.exception.NotFoundException;
import ph.txtdis.service.CredentialService;
import ph.txtdis.service.ServerUserService;

@Component("jpaAuthenticationManager")
public class JpaAuthenticationManager implements AuthenticationManager {

	@Autowired
	private CredentialService credentialService;

	@Autowired
	private ServerUserService userService;

	@Override
	public Authentication authenticate(Authentication auth) throws AuthenticationException {
		try {
			User user = userService.findByPrimaryKey(auth.getName());
			if (!credentialService.matchPasswords(auth, user))
				throw new NotFoundException("");
			credentialService.setAuthentication(user, (String) auth.getCredentials(), user.getRoles());
			return credentialService.authentication();
		} catch (NotFoundException e) {
			throw new BadCredentialsException("Incorrect Username and/or Password");
		}
	}
}
