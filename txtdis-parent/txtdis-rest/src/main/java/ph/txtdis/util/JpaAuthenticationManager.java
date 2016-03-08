package ph.txtdis.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Component;

import ph.txtdis.domain.User;
import ph.txtdis.repository.UserRepository;

@Component("jpaAuthenticationManager")
public class JpaAuthenticationManager implements AuthenticationManager {

	@Autowired
	private UserRepository userRepository;

	@Override
	public Authentication authenticate(Authentication auth) throws AuthenticationException {
		User user = userRepository.findOne(auth.getName());
		if (user != null && SpringUtils.matchPasswords(auth, user))
			return SpringUtils.authorize(user, auth, SpringUtils.toGranted(user.getRoles()));
		throw new BadCredentialsException("Incorrect Username and/or Password");
	}
}
