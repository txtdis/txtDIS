package ph.txtdis.service;

import java.util.List;

import ph.txtdis.dto.User;
import ph.txtdis.exception.FailedAuthenticationException;
import ph.txtdis.exception.InvalidException;
import ph.txtdis.exception.NoServerConnectionException;
import ph.txtdis.exception.NotAllowedOffSiteTransactionException;
import ph.txtdis.exception.RestException;
import ph.txtdis.exception.StoppedServerException;
import ph.txtdis.info.SuccessfulSaveInfo;
import ph.txtdis.type.UserType;

public interface UserService {

	boolean isOffSite();

	User find(String username) throws NoServerConnectionException, StoppedServerException, FailedAuthenticationException,
			RestException, InvalidException;

	User findByEmail(String email) throws NoServerConnectionException, StoppedServerException,
			FailedAuthenticationException, RestException, InvalidException;

	List<User> list() throws NoServerConnectionException, StoppedServerException, FailedAuthenticationException,
			RestException, InvalidException;

	List<User> listByRole(UserType... types) throws NoServerConnectionException, StoppedServerException,
			FailedAuthenticationException, RestException, InvalidException;

	List<String> listNamesByRole(UserType... types);

	User save(User entity) throws SuccessfulSaveInfo, NoServerConnectionException, StoppedServerException,
			FailedAuthenticationException, InvalidException, NotAllowedOffSiteTransactionException;

}