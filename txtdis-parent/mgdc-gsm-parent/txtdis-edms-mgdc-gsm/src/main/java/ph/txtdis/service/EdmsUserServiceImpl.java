package ph.txtdis.service;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import ph.txtdis.domain.EdmsSeller;
import ph.txtdis.dto.Authority;
import ph.txtdis.dto.User;
import ph.txtdis.type.UserType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service("userService")
public class EdmsUserServiceImpl //
	implements EdmsUserService {

	@Autowired
	private EdmsSellerService sellerService;

	private Authority collector, driver, dsp;

	private List<Authority> cashier, helper, manager;

	@Override
	public List<User> list() {
		setNewRoles();
		List<User> users = getDefaultUsers();
		users.addAll(getSalesUsers());
		return users;
	}

	private void setNewRoles() {
		cashier = Arrays.asList(newRole(UserType.CASHIER));
		collector = newRole(UserType.COLLECTOR);
		dsp = newRole(UserType.SELLER);
		driver = newRole(UserType.DRIVER);
		helper = Arrays.asList(newRole(UserType.HELPER));
		manager = Arrays.asList(newRole(UserType.MANAGER));
	}

	private Authority newRole(UserType role) {
		Authority a = new Authority();
		a.setAuthority(role);
		return a;
	}

	private List<User> getDefaultUsers() {
		return new ArrayList<>(Arrays.asList(//
			newUser("ANNALYN", "HIDALGO", "05201987", cashier), //
			newUser("PLASIBIL", "DAYAO", "Plasi09", cashier), //
			newUser("GELYN", "ABASCAR", "Ghelsie", cashier), //
			newUser("MARICEL", "LAYCO", "112987", cashier), //
			newUser("EDMS", "", "@eDMS", manager), //
			newUser("JACKIE", "SO", "robbie", manager), //
			newUser("RONALD", "SO", "alphacowboy", manager)));
	}

	private User newUser(String name, String surname, String password, List<Authority> roles) {
		User u = new User();
		u.setUsername(name);
		u.setSurname(surname);
		u.setPassword(encode(password));
		u.setRoles(roles);
		u.setEnabled(true);
		return u;
	}

	private List<User> getSalesUsers() {
		return sellerService.getAll().flatMap(s -> toUser(s).stream()).collect(Collectors.toList());
	}

	private List<User> toUser(EdmsSeller i) {
		List<User> users = new ArrayList<>();
		users.add(newUser(//
			sellerService.getUsername(i), //
			sellerService.getSurname(i), //
			Arrays.asList(dsp, collector)));

		if (!i.getPlateNo().isEmpty())
			users.add(newUser(//
				getUsername(i.getDriver()), //
				getSurname(i.getDriver()), //
				Arrays.asList(driver, collector)));

		if (!i.getHelper().isEmpty())
			users.add(newUser(//
				getUsername(i.getHelper()), //
				getSurname(i.getHelper()), //
				helper));

		return users;
	}

	private User newUser(String name, String surname, List<Authority> roles) {
		return newUser(name, surname, "password", roles);
	}

	private String encode(String password) {
		return new BCryptPasswordEncoder().encode(password);
	}

	@Override
	public String getUsername(String name) {
		return StringUtils.substringBefore(name, " ").toUpperCase().trim();
	}

	private String getSurname(String name) {
		return StringUtils.substringAfter(name, " ").toUpperCase().trim();
	}
}