package ph.txtdis.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ph.txtdis.dto.Keyed;
import ph.txtdis.exception.FailedAuthenticationException;
import ph.txtdis.exception.InvalidException;
import ph.txtdis.exception.NoServerConnectionException;
import ph.txtdis.exception.RestException;
import ph.txtdis.exception.StoppedServerException;

@Service("spunService")
public class SpunServiceImpl<T extends Keyed<PK>, PK> implements SpunService<T, PK> {

	@Autowired
	private ReadOnlyService<T> readOnlyService;

	private String module;

	@Override
	public SpunService<T, PK> module(String module) {
		this.module = module;
		return this;
	}

	@Override
	public T next(PK pk) throws NoServerConnectionException, StoppedServerException, FailedAuthenticationException,
			RestException, InvalidException {
		return getFirstForNewOrLastElseNext(pk);
	}

	@Override
	public T previous(PK id) throws NoServerConnectionException, StoppedServerException, FailedAuthenticationException,
			RestException, InvalidException {
		return getLastForNewOrFirst_ElsePrevious(id);
	}

	private T first() throws NoServerConnectionException, StoppedServerException, FailedAuthenticationException,
			RestException, InvalidException {
		return readOnlyService.module(module).getOne("/first");
	}

	private T getFirstForNewOrLastElseNext(PK id) throws NoServerConnectionException, StoppedServerException,
			FailedAuthenticationException, RestException, InvalidException {
		return id == null || isLast(id) ? first() : getNext(id);
	}

	private T getLastForNewOrFirst_ElsePrevious(PK id) throws NoServerConnectionException, StoppedServerException,
			FailedAuthenticationException, RestException, InvalidException {
		return id == null || isFirst(id) ? last() : getPrevious(id);
	}

	private T getNext(PK id) throws NoServerConnectionException, StoppedServerException, FailedAuthenticationException,
			RestException, InvalidException {
		return readOnlyService.module(module).getOne("/next?id=" + id);
	}

	private T getPrevious(PK id) throws NoServerConnectionException, StoppedServerException,
			FailedAuthenticationException, RestException, InvalidException {
		return readOnlyService.module(module).getOne("/previous?id=" + id);
	}

	private boolean isFirst(PK id) throws NoServerConnectionException, StoppedServerException,
			FailedAuthenticationException, RestException, InvalidException {
		T t = readOnlyService.module(module).getOne("/firstToSpin");
		return t.getId().equals(id);
	}

	private boolean isLast(PK id) throws NoServerConnectionException, StoppedServerException,
			FailedAuthenticationException, RestException, InvalidException {
		T t = readOnlyService.module(module).getOne("/lastToSpin");
		return t.getId().equals(id);
	}

	private T last() throws NoServerConnectionException, StoppedServerException, FailedAuthenticationException,
			RestException, InvalidException {
		return readOnlyService.module(module).getOne("/last");
	}
}
