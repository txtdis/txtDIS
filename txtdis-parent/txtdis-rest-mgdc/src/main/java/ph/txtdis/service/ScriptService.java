package ph.txtdis.service;

import java.util.List;

import ph.txtdis.dto.Script;
import ph.txtdis.exception.FailedReplicationException;

public interface ScriptService extends IdService<Script, Long> {

	Script getUnpostedTransactions();

	List<Script> listScripts();

	void runDownloadedScripts() throws FailedReplicationException;

	List<Script> save(List<Script> s);

	void tagSentScriptsAsSuch(List<Script> scripts);

	void write(List<Script> l) throws FailedReplicationException;
}
