package ph.txtdis.service;

import java.time.LocalDate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import lombok.Getter;
import ph.txtdis.exception.InvalidException;
import ph.txtdis.util.DateTimeUtils;

@Getter
@Service("syncService")
public class SyncServiceImpl implements SyncService {

	@Autowired
	private ReadOnlyService<String> readOnlyService;

	@Value("${app.version}")
	private String appVersion;

	private LocalDate serverDate;

	@Override
	public void validateServerAndClientDatesAreInSync() throws Exception {
		String date = readOnlyService.module("sync").getOne("/date");
		serverDate = DateTimeUtils.toDate(date);
		if (!serverDate.isEqual(LocalDate.now()))
			throw new InvalidException("Correct PC date to\n" + DateTimeUtils.toDateDisplay(serverDate));
	}

	@Override
	public void validateVersionIsLatest() throws Exception {
		String serverVersion = readOnlyService.module("sync").getOne("/serverVersion");
		String latestVersion = readOnlyService.module("sync").getOne("/latestVersion");
		if (latestVersion.compareTo(serverVersion) > 0)
			throw new InvalidException("Update server to\n" + latestVersion);
		if (latestVersion.compareTo(appVersion) > 0)
			throw new InvalidException("Update this to\n" + latestVersion);
	}
}
