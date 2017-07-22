package ph.txtdis.service;

import ph.txtdis.exception.FailedReplicationException;

public interface RestoreService {

	void restoreFromDownloadedBackup() throws FailedReplicationException;

}