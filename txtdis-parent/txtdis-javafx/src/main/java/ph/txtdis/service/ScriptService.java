package ph.txtdis.service;

import ph.txtdis.exception.FailedAuthenticationException;
import ph.txtdis.exception.InvalidException;
import ph.txtdis.exception.NoServerConnectionException;
import ph.txtdis.exception.StoppedServerException;
import ph.txtdis.type.ScriptType;

public interface ScriptService {

	void saveScripts()
			throws NoServerConnectionException, StoppedServerException, FailedAuthenticationException, InvalidException;

	void set(ScriptType scriptType, String script);

	boolean unpostedTransactionsExist();

}