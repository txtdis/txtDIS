package ph.txtdis.service;

import static org.springframework.security.core.context.SecurityContextHolder.getContext;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import ph.txtdis.dto.Authority;
import ph.txtdis.dto.User;
import ph.txtdis.type.UserType;

@Service("credentialService")
public class CredentialServiceImpl implements CredentialService {

	@Override
	public Authentication authentication() {
		return getContext().getAuthentication();
	}

	@Override
	public String encode(String password) {
		return new BCryptPasswordEncoder().encode(password);
	}

	@Override
	@SuppressWarnings("unchecked")
	public List<GrantedAuthority> getRoles() {
		return (List<GrantedAuthority>) authentication().getAuthorities();
	}

	@Override
	public boolean isAuthenticated() {
		return authentication() == null ? false : authentication().isAuthenticated();
	}

	@Override
	public boolean isUser(UserType user) {
		return getRoles().contains(new SimpleGrantedAuthority(user.toString()));
	}

	@Override
	public boolean matchPasswords(Authentication authenticate, User user) {
		return new BCryptPasswordEncoder().matches(authenticate.getCredentials().toString(), user.getPassword());
	}

	@Override
	public String password() {
		return authentication().getCredentials().toString();
	}

	@Override
	public void setCredentialsForValidation(String username, String password) {
		UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(username, password);
		getContext().setAuthentication(token);
	}

	@Override
	public void setAuthentication(User user, String password, List<Authority> roles) {
		setAuthentication(toGranted(roles), user, password);
	}

	private void setAuthentication(List<GrantedAuthority> roles, User user, String password) {
		UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(user, password, roles);
		getContext().setAuthentication(token);
	}

	@Override
	public void setPassword(String password) {
		setAuthentication(getRoles(), user(), password);
	}

	private List<GrantedAuthority> toGranted(List<Authority> roles) {
		return roles.stream().map(role -> new SimpleGrantedAuthority(role.getAuthority().toString())).collect(Collectors.toList());
	}

	@Override
	public void updateUser(User user) {
		setAuthentication(getRoles(), user, password());
	}

	@Override
	public User user() {
		return (User) authentication().getPrincipal();
	}

	@Override
	public String username() {
		return authentication().getName();
	}
}
