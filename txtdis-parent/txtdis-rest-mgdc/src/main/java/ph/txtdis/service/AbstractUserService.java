package ph.txtdis.service;

import java.util.List;
import java.util.stream.Collectors;

import ph.txtdis.domain.AuthorityEntity;
import ph.txtdis.domain.StyleEntity;
import ph.txtdis.domain.UserEntity;
import ph.txtdis.dto.Authority;
import ph.txtdis.dto.Style;
import ph.txtdis.dto.User;
import ph.txtdis.repository.UserRepository;
import ph.txtdis.type.UserType;

public class AbstractUserService extends AbstractIdService<UserRepository, UserEntity, User, String>
		implements UserService {

	@Override
	public User findByEmail(String email) {
		UserEntity e = repository.findByEmail(email);
		return toDTO(e);
	}

	@Override
	public List<User> list() {
		List<UserEntity> l = repository.findByEnabledTrueOrderByUsernameAsc();
		return toList(l);
	}

	@Override
	public List<User> listNamesByRole(List<UserType> types) {
		List<UserEntity> l = repository.findByEnabledTrueAndRolesAuthorityInOrderByUsernameAsc(types);
		return l == null ? null : l.stream().map(u -> nameOnlyUser(u)).collect(Collectors.toList());
	}

	private User nameOnlyUser(UserEntity user) {
		User u = new User();
		u.setUsername(user.getUsername());
		return u;
	}

	@Override
	protected User toDTO(UserEntity e) {
		if (e == null)
			return null;
		User u = new User();
		u.setUsername(e.getUsername());
		u.setPassword(e.getPassword());
		u.setSurname(e.getSurname());
		u.setRoles(convertAuthorityEntity(e.getRoles()));
		u.setStyle(convert(e.getStyle()));
		u.setEnabled(e.isEnabled());
		return u;
	}

	private Style convert(StyleEntity e) {
		if (e == null)
			return null;
		Style s = new Style();
		s.setBase(e.getBase());
		s.setFont(e.getFont());
		return s;
	}

	private List<Authority> convertAuthorityEntity(List<AuthorityEntity> l) {
		return l == null ? null : l.stream().map(a -> convert(a)).collect(Collectors.toList());
	}

	private Authority convert(AuthorityEntity e) {
		if (e == null)
			return null;
		Authority a = new Authority();
		a.setAuthority(e.getAuthority());
		return a;
	}

	@Override
	protected UserEntity toEntity(User t) {
		if (t == null)
			return null;
		UserEntity u = repository.findOne(t.getId());
		if (u == null)
			u = createNewUserEntity(t);
		if (u.isEnabled() && t.getEnabled() == true)
			u = updateUserEntity(t, u);
		else if (u.isEnabled() && t.getEnabled() == false)
			u.setEnabled(false);
		return u;
	}

	private UserEntity createNewUserEntity(User t) {
		UserEntity u = new UserEntity();
		u.setUsername(t.getUsername());
		u.setEnabled(true);
		return u;
	}

	private UserEntity updateUserEntity(User t, UserEntity u) {
		u.setPassword(t.getPassword());
		u.setStyle(convert(u, t.getStyle()));
		return u;
	}

	private StyleEntity convert(UserEntity u, Style t) {
		if (t == null)
			return null;
		StyleEntity s = u.getStyle();
		if (s == null)
			s = new StyleEntity();
		s.setBase(t.getBase());
		s.setFont(t.getFont());
		return s;
	}
}