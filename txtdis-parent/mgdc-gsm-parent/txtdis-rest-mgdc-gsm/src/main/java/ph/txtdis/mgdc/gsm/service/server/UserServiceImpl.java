package ph.txtdis.mgdc.gsm.service.server;

import static ph.txtdis.type.UserType.DRIVER;
import static ph.txtdis.type.UserType.HELPER;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ph.txtdis.dto.Authority;
import ph.txtdis.dto.User;
import ph.txtdis.service.AbstractUserService;
import ph.txtdis.service.ReadOnlyService;
import ph.txtdis.service.SavingService;
import ph.txtdis.type.UserType;

@Service("userService")
public class UserServiceImpl
		extends AbstractUserService //
		implements UserService {

	@Autowired
	private SavingService<User> savingService;

	@Autowired
	private ReadOnlyService<User> readOnlyService;

	@Override
	public void importAll() throws Exception {
		List<User> l = readOnlyService.module("user").getList();
		repository.save(toEntities(l));
	}

	@Override
	@Transactional
	public User save(User u) {
		try {
			u = super.save(u);
			return saveToEdms(u);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	private User saveToEdms(User u) throws Exception {
		List<Authority> roles = u.getRoles();
		if (is(roles, DRIVER))
			saveDriver(u);
		if (is(roles, HELPER))
			saveHelper(u);
		return u;
	}

	private boolean is(List<Authority> roles, UserType role) {
		return roles.stream().anyMatch(r -> r.getAuthority() == role);
	}

	@Override
	public void saveDriver(User u) throws Exception {
		savingService.module("driver").save(u);
	}

	@Override
	public void saveHelper(User u) throws Exception {
		savingService.module("helper").save(u);
	}
}