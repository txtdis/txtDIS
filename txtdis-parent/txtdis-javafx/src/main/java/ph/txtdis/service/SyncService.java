package ph.txtdis.service;

public interface SyncService {

	void validateServerAndClientDatesAreInSync() throws Exception;

	void validateVersionIsLatest() throws Exception;
}
