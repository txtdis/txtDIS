package ph.txtdis.util;

import static org.springframework.security.core.context.SecurityContextHolder.getContext;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import ph.txtdis.dto.Authority;
import ph.txtdis.dto.User;
import ph.txtdis.type.UserType;

public class UserUtils {

	private UserUtils() {
		throw new IllegalStateException("Utility class");
	}

	public static String encode(String password) {
		return new BCryptPasswordEncoder().encode(password);
	}

	public static boolean isAuthenticated() {
		return authentication() == null ? false : authentication().isAuthenticated();
	}

	public static Authentication authentication() {
		return getContext().getAuthentication();
	}

	public static boolean isUser(UserType user) {
		return getRoles().contains(new SimpleGrantedAuthority(user.toString()));
	}

	@SuppressWarnings("unchecked")
	public static List<GrantedAuthority> getRoles() {
		return (List<GrantedAuthority>) authentication().getAuthorities();
	}

	public static boolean matchPasswords(Authentication authenticate, User user) {
		return new BCryptPasswordEncoder().matches(authenticate.getCredentials().toString(), user.getPassword());
	}

	public static void setCredentialsForValidation(String username, String password) {
		UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(username, password);
		getContext().setAuthentication(token);
	}

	public static void setAuthentication(User user, String password, List<Authority> roles) {
		setAuthentication(toGranted(roles), user, password);
	}

	private static void setAuthentication(List<GrantedAuthority> roles, User user, String password) {
		UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(user, password, roles);
		getContext().setAuthentication(token);
	}

	private static List<GrantedAuthority> toGranted(List<Authority> roles) {
		return roles.stream().map(role -> new SimpleGrantedAuthority(role.getAuthority().toString()))
			.collect(Collectors.toList());
	}

	public static void setPassword(String password) {
		setAuthentication(getRoles(), user(), password);
	}

	public static User user() {
		return (User) authentication().getPrincipal();
	}

	public static void updateUser(User user) {
		setAuthentication(getRoles(), user, password());
	}

	public static String password() {
		return authentication().getCredentials().toString();
	}

	public static String username() {
		return authentication().getName();
	}
}
