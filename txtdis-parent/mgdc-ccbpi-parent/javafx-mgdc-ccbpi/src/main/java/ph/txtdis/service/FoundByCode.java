package ph.txtdis.service;

import ph.txtdis.exception.FailedAuthenticationException;
import ph.txtdis.exception.InvalidException;
import ph.txtdis.exception.NoServerConnectionException;
import ph.txtdis.exception.RestException;
import ph.txtdis.exception.StoppedServerException;

public interface FoundByCode<T> extends Moduled {

	default T findByCode(Long id) throws NoServerConnectionException, StoppedServerException,
			FailedAuthenticationException, InvalidException, RestException {
		return getReadOnlyService().module(getModule()).getOne("/code?of=" + id);
	}

	ReadOnlyService<T> getReadOnlyService();
}
