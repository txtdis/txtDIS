package ph.txtdis.service;

import java.time.LocalDate;

public interface SyncService {

	void validateServerAndClientDatesAreInSync() throws Exception;

	void validateVersionIsLatest() throws Exception;

	LocalDate getServerDate();
}
