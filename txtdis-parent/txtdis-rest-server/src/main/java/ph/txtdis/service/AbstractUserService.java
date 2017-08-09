package ph.txtdis.service;

import org.springframework.beans.factory.annotation.Autowired;
import ph.txtdis.domain.AuthorityEntity;
import ph.txtdis.domain.StyleEntity;
import ph.txtdis.domain.UserEntity;
import ph.txtdis.dto.Authority;
import ph.txtdis.dto.Style;
import ph.txtdis.dto.User;
import ph.txtdis.repository.RoleRepository;
import ph.txtdis.repository.UserRepository;
import ph.txtdis.type.UserType;

import java.util.List;
import java.util.stream.Collectors;

public class AbstractUserService //
	extends AbstractSavedReferencedKeyedService<UserRepository, UserEntity, User, String> //
	implements ServerUserService {

	@Autowired
	private RoleRepository roleRepository;

	@Override
	public User findByEmail(String email) {
		UserEntity e = repository.findByEmail(email);
		return toModel(e);
	}

	@Override
	protected User toModel(UserEntity e) {
		if (e == null)
			return null;
		User u = new User();
		u.setUsername(e.getName());
		u.setPassword(e.getPassword());
		u.setSurname(e.getSurname());
		u.setRoles(toRoles(e.getRoles()));
		u.setStyle(toStyle(e.getStyle()));
		u.setEnabled(e.isEnabled());
		return u;
	}

	private List<Authority> toRoles(List<AuthorityEntity> l) {
		return l == null ? null : l.stream().map(a -> toRole(a)).collect(Collectors.toList());
	}

	private Style toStyle(StyleEntity e) {
		if (e == null)
			return null;
		Style s = new Style();
		s.setBase(e.getBase());
		s.setFont(e.getFont());
		return s;
	}

	private Authority toRole(AuthorityEntity e) {
		if (e == null)
			return null;
		Authority a = new Authority();
		a.setId(e.getId());
		a.setAuthority(e.getRole());
		return a;
	}

	@Override
	public List<User> list() {
		List<UserEntity> l = repository.findByEnabledTrueOrderByNameAsc();
		return toModels(l);
	}

	@Override
	public List<User> listNamesByRole(List<UserType> types) {
		List<UserEntity> l = repository.findByEnabledTrueAndRolesRoleInOrderByNameAsc(types);
		return l == null ? null : l.stream().map(u -> nameOnlyUser(u)).collect(Collectors.toList());
	}

	private User nameOnlyUser(UserEntity user) {
		User u = new User();
		u.setUsername(user.getName());
		u.setSurname(user.getSurname());
		return u;
	}

	@Override
	protected UserEntity toEntity(User u) {
		if (u == null)
			return null;
		UserEntity e = repository.findOne(u.getId());
		if (e == null)
			e = newUser(u);
		if (e.isEnabled() && u.isEnabled())
			e = updateUser(u, e);
		else if (e.isEnabled() && !u.isEnabled())
			e.setEnabled(false);
		return e;
	}

	private UserEntity newUser(User u) {
		UserEntity e = new UserEntity();
		e.setName(u.getUsername());
		e.setSurname(u.getSurname());
		e.setEnabled(true);
		return e;
	}

	private UserEntity updateUser(User u, UserEntity e) {
		e.setPassword(u.getPassword());
		e.setRoles(roles(e, u.getRoles()));
		e.setStyle(toEntity(u.getStyle(), e.getStyle()));
		return e;
	}

	private List<AuthorityEntity> roles(UserEntity user, List<Authority> roles) {
		return roles == null ? null //
			: roles.stream().map(a -> toEntity(user, a.getAuthority())).collect(Collectors.toList());
	}

	private StyleEntity toEntity(Style s, StyleEntity e) {
		if (s == null)
			return null;
		if (e == null)
			e = new StyleEntity();
		e.setBase(s.getBase());
		e.setFont(s.getFont());
		return e;
	}

	private AuthorityEntity toEntity(UserEntity user, UserType role) {
		AuthorityEntity e = roleRepository.findByUserAndRole(user, role);
		return e == null ? newRole(user, role) : e;
	}

	private AuthorityEntity newRole(UserEntity user, UserType role) {
		AuthorityEntity e = new AuthorityEntity();
		e.setUser(user);
		e.setRole(role);
		return e;
	}
}