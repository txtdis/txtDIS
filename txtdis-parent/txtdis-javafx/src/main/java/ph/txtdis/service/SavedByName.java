package ph.txtdis.service;

import ph.txtdis.exception.FailedAuthenticationException;
import ph.txtdis.exception.InvalidException;
import ph.txtdis.exception.NoServerConnectionException;
import ph.txtdis.exception.NotAllowedOffSiteTransactionException;
import ph.txtdis.exception.StoppedServerException;
import ph.txtdis.info.SuccessfulSaveInfo;

public interface SavedByName<T> {

	T save(String name) throws SuccessfulSaveInfo, NoServerConnectionException, StoppedServerException,
			FailedAuthenticationException, InvalidException, NotAllowedOffSiteTransactionException;
}
