package ph.txtdis.service;

import ph.txtdis.exception.FailedReplicationException;
import ph.txtdis.exception.NoNewerFileException;
import ph.txtdis.type.SyncType;

import java.util.Date;

public interface DownloadService {

	Date download(SyncType type, Date latest) throws NoNewerFileException, FailedReplicationException;

}