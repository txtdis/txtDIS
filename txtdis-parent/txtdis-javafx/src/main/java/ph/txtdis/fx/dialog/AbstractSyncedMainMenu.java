package ph.txtdis.fx.dialog;

import org.springframework.beans.factory.annotation.Autowired;

import javafx.application.Platform;
import javafx.stage.WindowEvent;
import ph.txtdis.app.SyncApp;
import ph.txtdis.fx.control.AppButton;
import ph.txtdis.service.RestServerService;
import ph.txtdis.service.ScriptService;

public abstract class AbstractSyncedMainMenu extends AbstractMainMenu {

	@Autowired
	protected AppButton upButton, downButton;

	@Autowired
	protected SyncApp upApp, downApp;

	@Autowired
	private MessageDialog dialog;

	@Autowired
	private RestServerService restServerService;

	@Autowired
	private ScriptService scriptService;

	@Override
	protected void onCloseRequestAction(WindowEvent e) {
		if (restServerService.isOffSite() && scriptService.unpostedTransactionsExist())
			showPostOrExitDialog(e);
	}

	private void showPostOrExitDialog(WindowEvent we) {
		dialog.showOption("Unposted transactions exist;\nproceed, how?", "Post", "Exit");
		dialog.setOnOptionSelection(e -> postUnpostedTransaction(we));
		dialog.setOnDefaultSelection(e -> Platform.exit());
		dialog.addParent(this).start();
	}

	private void postUnpostedTransaction(WindowEvent e) {
		e.consume();
		dialog.close();
		upApp.start();
	}

	protected String upload() {
		return restServerService.isOffSite() ? "Post" : "Upload";
	}

	protected String download() {
		return restServerService.isOffSite() ? "Replicate" : "Download";
	}
}
