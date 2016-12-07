package ph.txtdis.service;

import ph.txtdis.exception.DuplicateException;
import ph.txtdis.exception.FailedAuthenticationException;
import ph.txtdis.exception.InvalidException;
import ph.txtdis.exception.NoServerConnectionException;
import ph.txtdis.exception.RestException;
import ph.txtdis.exception.StoppedServerException;

public interface UniquelyNamed<T> extends Moduled {

	default void confirmUniqueness(String name) throws NoServerConnectionException, StoppedServerException,
			FailedAuthenticationException, RestException, InvalidException, DuplicateException {
		if (findByName(name) != null)
			throw new DuplicateException(name);
	}

	default T findByName(String name) throws NoServerConnectionException, StoppedServerException,
			FailedAuthenticationException, RestException, InvalidException {
		return getListedReadOnlyService().module(getModule()).getOne("/" + name);
	}

	ReadOnlyService<T> getListedReadOnlyService();
}
