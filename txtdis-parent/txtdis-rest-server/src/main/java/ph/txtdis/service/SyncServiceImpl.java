package ph.txtdis.service;

import org.springframework.stereotype.Service;
import ph.txtdis.domain.SyncEntity;
import ph.txtdis.exception.FailedReplicationException;
import ph.txtdis.repository.SyncRepository;

import java.time.ZonedDateTime;

import static ph.txtdis.type.SyncType.UPDATE;
import static ph.txtdis.type.SyncType.VERSION;
import static ph.txtdis.util.DateTimeUtils.toZonedDateTime;
import static ph.txtdis.util.NumberUtils.divide;

@Service("syncService")
public class SyncServiceImpl //
	implements SyncService {

	private static final String UPLOAD_SUCCESSFUL = "Upload successful";

	private final BackupService backupService;

	private final UploadService uploadService;

	private final SyncRepository syncRepository;

	public SyncServiceImpl(BackupService backupService,
	                       UploadService uploadService,
	                       SyncRepository syncRepository) {
		this.backupService = backupService;
		this.uploadService = uploadService;
		this.syncRepository = syncRepository;
	}

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
