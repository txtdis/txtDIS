package ph.txtdis.service;

import static org.apache.log4j.Logger.getLogger;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component("scheduledTasks")
public class ScheduledTasks {

	private static Logger logger = getLogger(ScheduledTasks.class);

	@Autowired
	private PostDatedChequeService postDatedChequeService;

	@Autowired
	private ServerService serverService;

	@Autowired
	private SyncService syncService;

	private boolean uploadedBackup, updatedPostDatedChecks, valuatedInventories;

	@Scheduled(cron = "0 0 8/1 * * *")
	public void uploadBackup() {
		if (serverService.isMaster() && !uploadedBackup)
			try {
				syncService.upload();
				uploadedBackup = true;
			} catch (Exception e) {
				logger.error(e.getMessage());
			}
	}

	@Scheduled(cron = "0 15 8/1 * * *")
	public void downloadBackup() {
		if (serverService.isSlave())
			try {
				syncService.download();
			} catch (Exception e) {
				logger.error(e.getMessage());
			}
	}

	@Scheduled(cron = "0 30 8/1 * * *")
	public void uploadScripts() {
		if (serverService.isSlave())
			try {
				syncService.upload();
			} catch (Exception e) {
				logger.error(e.getMessage());
			}
	}

	@Scheduled(cron = "0 45 8/1 * * *")
	public void downloadScripts() {
		if (serverService.isMaster())
			try {
				syncService.download();
			} catch (Exception e) {
				logger.error(e.getMessage());
			}
	}

	@Scheduled(cron = "0 5 8/1 * * *")
	public void updatePostDatedChecks() {
		if (serverService.isMaster() && !updatedPostDatedChecks) {
			postDatedChequeService.setFullyPaidForMaturedPostDatedChecks();
			updatedPostDatedChecks = true;
		}
	}

	@Scheduled(cron = "0 10 8/1 * * *")
	public void valuateInventory() {
		if (serverService.isMaster() && !valuatedInventories) {
			// TODO
			valuatedInventories = true;
		}
	}
}
