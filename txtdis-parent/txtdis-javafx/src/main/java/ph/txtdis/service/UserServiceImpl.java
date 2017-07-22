package ph.txtdis.service;

import static java.util.Arrays.asList;
import static java.util.stream.Collectors.toList;
import static org.apache.log4j.Logger.getLogger;
import static ph.txtdis.type.UserType.CASHIER;
import static ph.txtdis.type.UserType.COLLECTOR;
import static ph.txtdis.type.UserType.DRIVER;
import static ph.txtdis.type.UserType.HELPER;
import static ph.txtdis.type.UserType.MANAGER;
import static ph.txtdis.type.UserType.SALES_ENCODER;
import static ph.txtdis.type.UserType.SELLER;
import static ph.txtdis.type.UserType.STOCK_CHECKER;
import static ph.txtdis.type.UserType.STORE_KEEPER;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;

import ph.txtdis.dto.Authority;
import ph.txtdis.dto.Role;
import ph.txtdis.dto.User;
import ph.txtdis.exception.NotFoundException;
import ph.txtdis.info.Information;
import ph.txtdis.info.SuccessfulSaveInfo;
import ph.txtdis.type.UserType;

@Service("userService")
public class UserServiceImpl implements UserService {

	private static Logger logger = getLogger(UserServiceImpl.class);

	private static final String USER = "user";

	@Autowired
	private CredentialService userService;

	@Autowired
	private ReadOnlyService<User> readOnlyService;

	@Autowired
	private SavingService<User> savingService;

	private User user;

	@Override
	public User find(String username) throws Exception {
		return readOnlyService.module(USER).getOne("/" + username);
	}

	private User set(User u) {
		return user = u;
	}

	@Override
	public User findByEmail(String email) throws Exception {
		return readOnlyService.module(USER).getOne("/email?address=" + email);
	}

	private User get() {
		if (user == null)
			user = new User();
		return user;
	}

	@Override
	public List<Role> getRolesThatCanBeAssigned() {
		List<GrantedAuthority> l = userService.getRoles();
		if (isOneOfUsersRoleA(CASHIER, l))
			return getRolesThatACashierCanAssign();
		if (isOneOfUsersRoleA(MANAGER, l))
			return getRolesThatAManagerCanAssign();
		if (isOneOfUsersRoleA(SALES_ENCODER, l))
			return getRolesThatASalesEncoderCanAssign();
		if (isOneOfUsersRoleA(STOCK_CHECKER, l))
			return getRolesThatAStockCheckerCanAssign();
		return Collections.emptyList();
	}

	private boolean isOneOfUsersRoleA(UserType user, List<GrantedAuthority> l) {
		return l.stream().anyMatch(a -> a.getAuthority().equalsIgnoreCase(user.toString()));
	}

	private List<Authority> getRoles() {
		if (get().getRoles() == null)
			get().setRoles(Collections.emptyList());
		return get().getRoles();
	}

	private List<Role> getRolesThatACashierCanAssign() {
		return getRoles(CASHIER, COLLECTOR);
	}

	private List<Role> getRoles(UserType... users) {
		return asList(users).stream().map(u -> toRole(u, user.getRoles())).collect(Collectors.toList());
	}

	private Role toRole(UserType u, List<Authority> l) {
		Role r = new Role();
		r.setAuthority(u);
		r.setEnabled(isRoleEnabled(u, l));
		return r;
	}

	private boolean isRoleEnabled(UserType u, List<Authority> l) {
		return l == null ? false : l.stream().anyMatch(a -> a.getAuthority() == u);
	}

	private List<Role> getRolesThatAManagerCanAssign() {
		return getRoles(UserType.values());
	}

	private List<Role> getRolesThatASalesEncoderCanAssign() {
		return getRoles(SALES_ENCODER, SELLER);
	}

	private List<Role> getRolesThatAStockCheckerCanAssign() {
		return getRoles(STOCK_CHECKER, STORE_KEEPER, DRIVER, HELPER);
	}

	@Override
	public String getSurname() {
		return get().getSurname();
	}

	@Override
	public String getUsername() {
		return get().getUsername();
	}

	@Override
	public boolean isEnabled() {
		return get().isEnabled();
	}

	@Override
	public List<User> list() throws Exception {
		return readOnlyService.module(USER).getList();
	}

	@Override
	public List<User> listByRole(UserType... types) throws Exception {
		String roles = "/role?";
		for (int i = 0; i < types.length; i++)
			roles += (i == 0 ? "" : "&") + "name=" + types[i];
		return readOnlyService.module(USER).getList(roles);
	}

	@Override
	public List<String> listNamesByRole(UserType... types) {
		try {
			return listByRole(types).stream().map(u -> u.getUsername()).collect(toList());
		} catch (Exception e) {
			return null;
		}
	}

	@Override
	public void reset() {
		set(null);
	}

	@Override
	public void save(List<Role> roles) throws Information, Exception {
		setRoles(roles);
		setPasswordIfNone();
		save(get());
		throw new SuccessfulSaveInfo(getUsername());
	}

	private void setPasswordIfNone() {
		if (get().getPassword() == null)
			get().setPassword(userService.encode("password"));
	}

	@Override
	public User save(User entity) throws Exception {
		return set(savingService.module(USER).save(entity));
	}

	@Override
	public void setEnabled(boolean b) {
		get().setEnabled(b);
	}

	@Override
	public void setRoles(List<Role> roles) {
		if (roles == null)
			return;
		List<Authority> l = new ArrayList<>(getRoles());
		l = removeRoles(l, roles);
		l = addRoles(l, roles);
		get().setRoles(l);
	}

	private List<Authority> removeRoles(List<Authority> authorities, List<Role> roles) {
		List<UserType> disabledRoles = roles.stream() //
				.filter(r -> r.getEnabled() == false) //
				.map(r -> r.getAuthority()) //
				.collect(Collectors.toList());
		logger.info("\n    DisabledRoles = " + disabledRoles);
		authorities.removeIf(a -> disabledRoles.contains(a.getAuthority()));
		logger.info("\n    RemainingRoles = " + authorities);
		return authorities;
	}

	private List<Authority> addRoles(List<Authority> authorities, List<Role> roles) {
		List<Authority> l = roles.stream() //
				.filter(r -> r.getEnabled() == true) //
				.map(r -> toAuthority(r)) //
				.filter(a -> isNotIn(a, authorities)) //
				.collect(Collectors.toList());
		logger.info("\n    NewRoles = " + l);
		if (!l.isEmpty())
			authorities.addAll(l);
		logger.info("\n    TotalRoles = " + authorities);
		return authorities;
	}

	private Authority toAuthority(Role r) {
		Authority a = new Authority();
		a.setAuthority(r.getAuthority());
		return a;
	}

	private boolean isNotIn(Authority a, List<Authority> authorities) {
		return !authorities.stream().anyMatch(i -> i.getAuthority() == a.getAuthority());
	}

	@Override
	public void setSurname(String surname) {
		get().setSurname(surname);
	}

	@Override
	public void validateUsername(String username) throws Exception {
		User user = find(username);
		if (user == null) {
			get().setUsername(username);
			throw new NotFoundException("");
		}
		set(user);
	}
}
