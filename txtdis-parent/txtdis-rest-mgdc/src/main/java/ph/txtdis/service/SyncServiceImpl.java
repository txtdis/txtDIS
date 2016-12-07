package ph.txtdis.service;

import static java.time.LocalDate.now;
import static org.apache.log4j.Logger.getLogger;
import static ph.txtdis.type.SyncType.BACKUP;
import static ph.txtdis.type.SyncType.SCRIPT;
import static ph.txtdis.type.SyncType.VERSION;
import static ph.txtdis.util.DateTimeUtils.epochDate;
import static ph.txtdis.util.DateTimeUtils.toUtilDate;
import static ph.txtdis.util.DateTimeUtils.toZonedDateTime;

import java.time.ZonedDateTime;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ph.txtdis.controller.SyncController;
import ph.txtdis.domain.SyncEntity;
import ph.txtdis.dto.Script;
import ph.txtdis.exception.FailedReplicationException;
import ph.txtdis.exception.NoNewerFileException;
import ph.txtdis.repository.SyncRepository;

@Service("syncService")
public class SyncServiceImpl implements SyncService {

	private static final String SUCCESSFUL = "successful";

	private static final String UPDATE_SUCCESSFUL = "Update " + SUCCESSFUL;

	private static final String REPLICATION_SUCCESSFUL = "Replication " + SUCCESSFUL;

	private static final String UPLOAD_SUCCESSFUL = "Upload " + SUCCESSFUL;

	private static Logger logger = getLogger(SyncController.class);

	@Autowired
	private DownloadService downloadService;

	@Autowired
	private BackupService backupService;

	@Autowired
	private UploadService uploadService;

	@Autowired
	private RestoreService restoreService;

	@Autowired
	private ServerService serverService;

	@Autowired
	private ScriptService scriptService;

	@Autowired
	private SyncRepository syncRepository;

	private List<Script> scripts;

	@Override
	public String download() throws FailedReplicationException, NoNewerFileException, FailedReplicationException {
		logger.info("Server is a slave = " + serverService.isSlave());
		return serverService.isSlave() ? restoreBackup() : runScripts();
	}

	@Override
	public String upload() throws FailedReplicationException {
		logger.info("Server is a master = " + serverService.isMaster());
		if (serverService.isMaster())
			return uploadBackup();
		if (scriptsExist())
			return uploadScript();
		return "Nothing to upload";
	}

	@Override
	public void updateAppVersion(String[] s) {
		SyncEntity sync = syncRepository.findByType(VERSION);
		sync.setLastSync(toUtilDate(s[2]));
		syncRepository.save(sync);
	}

	@Override
	public String getLatestVersion() {
		SyncEntity sync = syncRepository.findByType(VERSION);
		ZonedDateTime zdt = toZonedDateTime(sync.getLastSync());
		return "0." + (zdt.getYear() - 2000) + "." + zdt.getMonthValue() + "." + zdt.getDayOfMonth();
	}

	private String uploadBackup() throws FailedReplicationException {
		backupService.backup();
		uploadService.upload("backup");
		return UPLOAD_SUCCESSFUL;
	}

	private SyncEntity getLatestSync() {
		SyncEntity sync = syncRepository.findByType(SCRIPT);
		return sync != null ? sync : new SyncEntity(SCRIPT, epochDate());
	}

	private String restoreBackup() throws NoNewerFileException, FailedReplicationException {
		downloadService.download(BACKUP, toUtilDate(now()));
		restoreService.restoreFromDownloadedBackup();
		return REPLICATION_SUCCESSFUL;
	}

	public String runScripts() throws NoNewerFileException, FailedReplicationException {
		SyncEntity sync = getLatestSync();
		Date latest = sync.getLastSync();
		latest = downloadService.download(SCRIPT, latest);
		scriptService.runDownloadedScripts();

		sync.setLastSync(latest);
		syncRepository.save(sync);
		return UPDATE_SUCCESSFUL;
	}

	private boolean scriptsExist() {
		scripts = scriptService.listScripts();
		logger.info("Scripts = " + scripts);
		return scripts != null || scripts.isEmpty();
	}

	private String uploadScript() throws FailedReplicationException {
		scriptService.write(scripts);
		uploadService.upload("script");
		scriptService.tagSentScriptsAsSuch(scripts);
		return UPLOAD_SUCCESSFUL;
	}
}
