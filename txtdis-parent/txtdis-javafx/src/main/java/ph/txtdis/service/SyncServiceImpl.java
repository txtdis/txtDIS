package ph.txtdis.service;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ph.txtdis.exception.InvalidException;

import java.time.LocalDate;

import static ph.txtdis.util.DateTimeUtils.*;

@Getter
@Service("syncService")
public class SyncServiceImpl
	implements SyncService {

	@Autowired
	private RestClientService<String> restClientService;

	@Value("${app.version}")
	private String appVersion;

	@Override
	public void validateServerAndClientDatesAreInSync() throws Exception {
		String date = findSyncDate("/date");
		LocalDate serverDate = toDate(date);
		if (!serverDate.isEqual(LocalDate.now()))
			throw new InvalidException("Correct PC date to\n" + toDateDisplay(serverDate));
		setServerDate(serverDate);
	}

	private String findSyncDate(String endpoint) throws Exception {
		return restClientService.module("sync").getOne(endpoint);
	}

	@Override
	public void validateVersionIsLatest() throws Exception {
		String serverVersion = findSyncDate("/serverVersion");
		String latestVersion = findSyncDate("/latestVersion");
		if (latestVersion.compareTo(serverVersion) > 0)
			throw new InvalidException("Update server to\n" + latestVersion);
		if (latestVersion.compareTo(appVersion) > 0)
			throw new InvalidException("Update this to\n" + latestVersion);
	}
}
