package ph.txtdis.service;

import java.util.List;

import ph.txtdis.exception.FailedAuthenticationException;
import ph.txtdis.exception.InvalidException;
import ph.txtdis.exception.NoServerConnectionException;
import ph.txtdis.exception.RestException;
import ph.txtdis.exception.StoppedServerException;

public interface ReadOnlyService<T> {

	List<T> getList() throws NoServerConnectionException, StoppedServerException, FailedAuthenticationException,
			RestException, InvalidException;

	List<T> getList(String string) throws NoServerConnectionException, StoppedServerException,
			FailedAuthenticationException, RestException, InvalidException;

	T getOne(String endpoint) throws NoServerConnectionException, StoppedServerException, FailedAuthenticationException,
			RestException, InvalidException;

	ReadOnlyService<T> module(String module);
}