package ph.txtdis.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Component;
import ph.txtdis.dto.User;
import ph.txtdis.exception.NotFoundException;
import ph.txtdis.service.ServerUserService;

import static ph.txtdis.util.UserUtils.*;

@Component("jpaAuthenticationManager")
public class JpaAuthenticationManager
	implements AuthenticationManager {

	@Autowired
	private ServerUserService userService;

	@Override
	public Authentication authenticate(Authentication auth) throws AuthenticationException {
		try {
			User user = userService.findByPrimaryKey(auth.getName());
			if (!matchPasswords(auth, user))
				throw new NotFoundException("");
			setAuthentication(user, (String) auth.getCredentials(), user.getRoles());
			return authentication();
		} catch (NotFoundException e) {
			throw new BadCredentialsException("Incorrect Username and/or Password");
		}
	}
}
