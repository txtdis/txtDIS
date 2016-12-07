package ph.txtdis.service;

import ph.txtdis.exception.FailedReplicationException;
import ph.txtdis.exception.NoNewerFileException;

public interface SyncService {

	String download() throws FailedReplicationException, NoNewerFileException, FailedReplicationException;

	String upload() throws FailedReplicationException;

	String getLatestVersion();

	void updateAppVersion(String[] s);
}
