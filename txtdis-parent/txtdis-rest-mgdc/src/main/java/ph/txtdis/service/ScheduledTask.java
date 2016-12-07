package ph.txtdis.service;

public interface ScheduledTask {

	void uploadBackup();

	void downloadBackup();

	void uploadScripts();

	void downloadScripts();

	void updatePostDatedChecks();

	void valuateInventory();
}