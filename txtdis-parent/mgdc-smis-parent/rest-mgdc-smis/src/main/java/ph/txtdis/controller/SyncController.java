package ph.txtdis.controller;

import static org.springframework.http.HttpStatus.OK;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import ph.txtdis.exception.FailedReplicationException;
import ph.txtdis.exception.NoNewerFileException;
import ph.txtdis.service.SyncService;

@RequestMapping("/syncs")
@RestController("syncController")
public class SyncController {

	@Autowired
	private SyncService sync;

	@Value("${app.version}")
	private String appVersion;

	@RequestMapping(path = "/download")
	public ResponseEntity<?> download()
			throws FailedReplicationException, NoNewerFileException, FailedReplicationException {
		return new ResponseEntity<>(sync.download(), OK);
	}

	@RequestMapping(path = "/upload")
	public ResponseEntity<?> upload() throws FailedReplicationException {
		return new ResponseEntity<>(sync.upload(), OK);
	}

	@RequestMapping(path = "/latestVersion")
	public ResponseEntity<?> getLatestAppVersion() {
		return new ResponseEntity<>(sync.getLatestVersion(), OK);
	}

	@RequestMapping(path = "/serverVersion")
	public ResponseEntity<?> getServerVersion() {
		return new ResponseEntity<>(appVersion, OK);
	}
}