package ph.txtdis.service;

import static ph.txtdis.type.SyncType.UPDATE;
import static ph.txtdis.type.SyncType.VERSION;
import static ph.txtdis.util.DateTimeUtils.toZonedDateTime;
import static ph.txtdis.util.NumberUtils.divide;

import java.time.ZonedDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ph.txtdis.domain.SyncEntity;
import ph.txtdis.exception.FailedReplicationException;
import ph.txtdis.repository.SyncRepository;

@Service("syncService")
public class SyncServiceImpl //
		implements SyncService {

	private static final String UPLOAD_SUCCESSFUL = "Upload successful";

	@Autowired
	private BackupService backupService;

	@Autowired
	private UploadService uploadService;

	@Autowired
	private SyncRepository syncRepository;

	@Override
	public String upload() throws FailedReplicationException {
		backupService.backup();
		uploadService.upload("backup");
		return UPLOAD_SUCCESSFUL;
	}

	@Override
	public String getLatestVersion() {
		SyncEntity sync = syncRepository.findByType(VERSION);
		return version(sync);
	}

	private String version(SyncEntity sync) {
		ZonedDateTime zdt = toZonedDateTime(sync.getLastSync());
		return divide(zdt.getYear() - 2000, 10).setScale(1) + "." + zdt.getMonthValue() + "." + zdt.getDayOfMonth();
	}

	@Override
	public String getUpdateVersion() {
		SyncEntity sync = syncRepository.findByType(UPDATE);
		return sync == null ? "0.0.0.0" : version(sync);
	}
}
