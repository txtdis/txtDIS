package ph.txtdis.controller;

import static org.springframework.http.HttpStatus.OK;

import java.time.LocalDate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import ph.txtdis.exception.FailedReplicationException;
import ph.txtdis.service.SyncService;

@RequestMapping("/syncs")
@RestController("syncController")
public class SyncController {

	@Autowired
	private SyncService service;

	@Value("${app.version}")
	private String appVersion;

	@RequestMapping(path = "/date")
	public ResponseEntity<?> date() throws FailedReplicationException {
		return new ResponseEntity<>(LocalDate.now().toString(), OK);
	}

	@RequestMapping(path = "/latestVersion")
	public ResponseEntity<?> latestVersion() {
		return new ResponseEntity<>(service.getLatestVersion(), OK);
	}

	@RequestMapping(path = "/serverVersion")
	public ResponseEntity<?> serverVersion() {
		return new ResponseEntity<>(appVersion, OK);
	}

	@RequestMapping(path = "/upload")
	public ResponseEntity<?> upload() throws FailedReplicationException {
		return new ResponseEntity<>(service.upload(), OK);
	}
}