package ph.txtdis.service;

import ph.txtdis.exception.FailedAuthenticationException;
import ph.txtdis.exception.InvalidException;
import ph.txtdis.exception.NoServerConnectionException;
import ph.txtdis.exception.StoppedServerException;

public interface SavingService<T> {

	T save(T entity)
			throws NoServerConnectionException, StoppedServerException, FailedAuthenticationException, InvalidException;

	SavingService<T> module(String billable);
}