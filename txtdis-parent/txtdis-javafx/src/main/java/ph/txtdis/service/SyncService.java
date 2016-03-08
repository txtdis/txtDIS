package ph.txtdis.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import lombok.Getter;
import ph.txtdis.exception.FailedAuthenticationException;
import ph.txtdis.exception.InvalidException;
import ph.txtdis.exception.NoServerConnectionException;
import ph.txtdis.exception.RestException;
import ph.txtdis.exception.StoppedServerException;

@Getter
@Service("syncService")
public class SyncService {

	@Autowired
	private ReadOnlyService<String> readOnlyService;
	
	@Value("${app.version}")
	private String appVersion;

	public void validateVersionIsLatest() throws NoServerConnectionException, StoppedServerException,
			FailedAuthenticationException, RestException, InvalidException {
		String serverVersion = readOnlyService.module("sync").getOne("/serverVersion");
		String latestVersion = readOnlyService.module("sync").getOne("/latestVersion");
		if (latestVersion.compareTo(serverVersion) > 0)
			throw new InvalidException("Update server to\n" + latestVersion);
		if (latestVersion.compareTo(appVersion) > 0)
			throw new InvalidException("Update this to\n" + latestVersion);
	}
}
