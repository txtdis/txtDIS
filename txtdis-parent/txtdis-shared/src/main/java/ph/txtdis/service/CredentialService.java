package ph.txtdis.service;

import java.util.List;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;

import ph.txtdis.dto.Authority;
import ph.txtdis.dto.User;
import ph.txtdis.type.UserType;

public interface CredentialService {

	Authentication authentication();

	String encode(String password);

	List<GrantedAuthority> getRoles();

	boolean isAuthenticated();

	boolean isUser(UserType user);

	boolean matchPasswords(Authentication authenticate, User user);

	String password();

	void setAuthentication(User user, String password, List<Authority> roles);

	void setCredentialsForValidation(String username, String password);

	void setPassword(String password);

	void updateUser(User user);

	User user();

	String username();

}