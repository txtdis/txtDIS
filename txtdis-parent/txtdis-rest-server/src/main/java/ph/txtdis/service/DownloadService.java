package ph.txtdis.service;

import java.util.Date;

import ph.txtdis.exception.FailedReplicationException;
import ph.txtdis.exception.NoNewerFileException;
import ph.txtdis.type.SyncType;

public interface DownloadService {

	Date download(SyncType type, Date latest) throws NoNewerFileException, FailedReplicationException;

}