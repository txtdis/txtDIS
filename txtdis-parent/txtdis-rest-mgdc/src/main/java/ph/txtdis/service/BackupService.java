package ph.txtdis.service;

import ph.txtdis.exception.FailedReplicationException;

public interface BackupService {

	void backup() throws FailedReplicationException;

	byte[] getBackupBytes() throws FailedReplicationException;

}