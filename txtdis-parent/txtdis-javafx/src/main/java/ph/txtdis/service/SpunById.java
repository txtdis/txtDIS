package ph.txtdis.service;

import ph.txtdis.dto.CreationTracked;
import ph.txtdis.dto.Keyed;
import ph.txtdis.exception.FailedAuthenticationException;
import ph.txtdis.exception.InvalidException;
import ph.txtdis.exception.NoServerConnectionException;
import ph.txtdis.exception.RestException;
import ph.txtdis.exception.StoppedServerException;

public interface SpunById<PK> extends CreationTracked, GetSet<PK>, Keyed<PK>, Moduled, Spun {

	default PK getSpunId() {
		return isNew() ? null : getId();
	}

	default String getSpunModule() {
		return getModule();
	}

	SpunService<? extends Keyed<PK>, PK> getSpunService();

	default boolean isNew() {
		return getCreatedOn() == null;
	}

	@Override
	default void next() throws NoServerConnectionException, StoppedServerException, FailedAuthenticationException,
			RestException, InvalidException {
		set(getSpunService().module(getSpunModule()).next(getSpunId()));
	}

	@Override
	default void previous() throws NoServerConnectionException, StoppedServerException, FailedAuthenticationException,
			RestException, InvalidException {
		set(getSpunService().module(getSpunModule()).previous(getSpunId()));
	}
}
