package ph.txtdis.service;

import ph.txtdis.exception.DuplicateException;
import ph.txtdis.exception.FailedAuthenticationException;
import ph.txtdis.exception.InvalidException;
import ph.txtdis.exception.NoServerConnectionException;
import ph.txtdis.exception.RestException;
import ph.txtdis.exception.StoppedServerException;

public interface Unique<T> extends FoundByCode<T> {

	default void duplicateExists(Long id) throws NoServerConnectionException, StoppedServerException,
			FailedAuthenticationException, InvalidException, DuplicateException, RestException {
		if (findByCode(id) != null)
			throw new DuplicateException("Code " + id);
	}
}
