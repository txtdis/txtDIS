package ph.txtdis.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ph.txtdis.dto.User;
import ph.txtdis.exception.FailedAuthenticationException;
import ph.txtdis.exception.InvalidException;
import ph.txtdis.exception.NoServerConnectionException;
import ph.txtdis.exception.RestException;
import ph.txtdis.exception.StoppedServerException;

@Service("userService")
public class UserServiceImpl extends AbstractUserService implements ImportedUserService {

	@Autowired
	private ReadOnlyService<User> readOnlyService;

	@Override
	public void importAll() throws NoServerConnectionException, StoppedServerException, FailedAuthenticationException,
			RestException, InvalidException {
		List<User> l = readOnlyService.module("user").getList();
		repository.save(toEntities(l));
	}
}