package ph.txtdis.mgdc.gsm.service.server;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component("scheduledPostDatedCheckUpdateTask")
public class ScheduledPostDatedCheckUpdateTaskImpl //
	implements ScheduledPostDatedCheckUpdateTask {

	@Autowired
	private PostDatedChequeUpdateService chequeService;

	private boolean updatedChecks;

	@Override
	@Scheduled(cron = "0 5 8/1 * * *")
	public void updatePostDatedChecks() {
		if (!updatedChecks) {
			chequeService.setFullyPaidForMaturedPostDatedChecks();
			updatedChecks = true;
		}
	}
}
