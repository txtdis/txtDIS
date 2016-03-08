package ph.txtdis.service;

import java.time.LocalDate;

import ph.txtdis.dto.Keyed;
import ph.txtdis.exception.FailedAuthenticationException;
import ph.txtdis.exception.InvalidException;
import ph.txtdis.exception.NoServerConnectionException;
import ph.txtdis.exception.NotFoundException;
import ph.txtdis.exception.RestException;
import ph.txtdis.exception.StoppedServerException;
import ph.txtdis.util.DateTimeUtils;

public interface Serviced<PK> extends AlternateNamed, Iconed, SpunById<PK>, Saved<PK> {

	@SuppressWarnings("unchecked")
	default <T> T find(LocalDate d) throws NoServerConnectionException, StoppedServerException,
			FailedAuthenticationException, RestException, InvalidException, NotFoundException {
		T e = (T) getReadOnlyService().module(getSpunModule()).getOne("/date?on=" + d);
		if (e == null)
			throw new NotFoundException(getHeaderText() + " dated " + DateTimeUtils.toDateDisplay(d));
		return e;
	}

	default <T> T find(Long id) throws NoServerConnectionException, StoppedServerException,
			FailedAuthenticationException, RestException, InvalidException, NotFoundException {
		return find(id.toString());
	}

	@SuppressWarnings("unchecked")
	default <T> T find(String id) throws NoServerConnectionException, StoppedServerException,
			FailedAuthenticationException, RestException, InvalidException, NotFoundException {
		String endpt = "/" + (getSpunModule().equals(getModule()) ? id : getSpunModule() + "?id=" + id);
		T e = (T) getReadOnlyService().module(getModule()).getOne(endpt);
		if (e == null)
			throw new NotFoundException(getModuleId() + id);
		return e;
	}

	<T extends Keyed<PK>> ReadOnlyService<T> getReadOnlyService();

	default void open(LocalDate d) throws NoServerConnectionException, StoppedServerException,
			FailedAuthenticationException, RestException, InvalidException, NotFoundException {
		set(find(d));
	}

	default void open(Long id) throws NoServerConnectionException, StoppedServerException, FailedAuthenticationException,
			RestException, InvalidException, NotFoundException {
		set(find(id));
	}

	default void open(String id) throws NoServerConnectionException, StoppedServerException,
			FailedAuthenticationException, RestException, InvalidException, NotFoundException {
		set(find(id));
	}
}
