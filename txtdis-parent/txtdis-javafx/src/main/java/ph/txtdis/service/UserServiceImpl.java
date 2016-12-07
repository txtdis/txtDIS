package ph.txtdis.service;

import static java.util.stream.Collectors.toList;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ph.txtdis.dto.User;
import ph.txtdis.exception.FailedAuthenticationException;
import ph.txtdis.exception.InvalidException;
import ph.txtdis.exception.NoServerConnectionException;
import ph.txtdis.exception.NotAllowedOffSiteTransactionException;
import ph.txtdis.exception.RestException;
import ph.txtdis.exception.StoppedServerException;
import ph.txtdis.info.SuccessfulSaveInfo;
import ph.txtdis.type.UserType;

@Service("userService")
public class UserServiceImpl implements UserService {

	private static final String USER = "user";

	@Autowired
	private ReadOnlyService<User> readOnlyService;

	@Autowired
	private RestServerService serverService;

	@Autowired
	private SavingService<User> savingService;

	@Override
	public User find(String username) throws NoServerConnectionException, StoppedServerException,
			FailedAuthenticationException, RestException, InvalidException {
		return readOnlyService.module(USER).getOne("/" + username);
	}

	@Override
	public User findByEmail(String email) throws NoServerConnectionException, StoppedServerException,
			FailedAuthenticationException, RestException, InvalidException {
		return readOnlyService.module(USER).getOne("/email?address=" + email);
	}

	@Override
	public boolean isOffSite() {
		return serverService.isOffSite();
	}

	@Override
	public List<User> list() throws NoServerConnectionException, StoppedServerException, FailedAuthenticationException,
			RestException, InvalidException {
		return readOnlyService.module(USER).getList();
	}

	@Override
	public List<User> listByRole(UserType... types) throws NoServerConnectionException, StoppedServerException,
			FailedAuthenticationException, RestException, InvalidException {
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
	public User save(User entity) throws SuccessfulSaveInfo, NoServerConnectionException, StoppedServerException,
			FailedAuthenticationException, InvalidException, NotAllowedOffSiteTransactionException {
		if (isOffSite())
			throw new NotAllowedOffSiteTransactionException();
		return savingService.module(USER).save(entity);
	}
}
