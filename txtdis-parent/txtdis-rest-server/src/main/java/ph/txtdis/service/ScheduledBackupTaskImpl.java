package ph.txtdis.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component("scheduledBackupTask")
public class ScheduledBackupTaskImpl implements ScheduledBackupTask {

	@Autowired
	private SyncService syncService;

	private boolean uploadedBackup;

	@Override
	@Scheduled(cron = "0 0 8/1 * * *")
	public void uploadBackup() {
		if (!uploadedBackup)
			try {
				syncService.upload();
				uploadedBackup = true;
			} catch (Exception e) {
				e.printStackTrace();
			}
	}
}
