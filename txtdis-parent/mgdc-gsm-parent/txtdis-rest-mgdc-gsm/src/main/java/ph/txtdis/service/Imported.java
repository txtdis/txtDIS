package ph.txtdis.service;

import ph.txtdis.exception.FailedAuthenticationException;
import ph.txtdis.exception.InvalidException;
import ph.txtdis.exception.NoServerConnectionException;
import ph.txtdis.exception.RestException;
import ph.txtdis.exception.StoppedServerException;

public interface Imported {

	void importAll() throws NoServerConnectionException, StoppedServerException, FailedAuthenticationException,
			RestException, InvalidException;
}
