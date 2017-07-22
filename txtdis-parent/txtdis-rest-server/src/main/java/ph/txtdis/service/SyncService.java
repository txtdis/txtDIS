package ph.txtdis.service;

import ph.txtdis.exception.FailedReplicationException;

public interface SyncService {

	String upload() throws FailedReplicationException;

	String getLatestVersion();

	String getUpdateVersion();
}
