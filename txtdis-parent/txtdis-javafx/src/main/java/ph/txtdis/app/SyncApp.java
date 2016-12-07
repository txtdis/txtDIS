package ph.txtdis.app;

import static org.apache.commons.lang3.StringUtils.startsWith;
import static ph.txtdis.type.BillableType.DOWNLOAD;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javafx.concurrent.Task;
import javafx.stage.Stage;
import ph.txtdis.fx.dialog.MessageDialog;
import ph.txtdis.fx.dialog.ProgressDialog;
import ph.txtdis.service.ReadOnlyService;
import ph.txtdis.service.RestServerService;
import ph.txtdis.service.ScriptService;
import ph.txtdis.type.BillableType;

@Scope("prototype")
@Component("syncApp")
public class SyncApp extends Stage implements MultiTyped, Startable {

	@Autowired
	private ReadOnlyService<String> readOnlyService;

	@Autowired
	private ScriptService scriptService;

	@Autowired
	private MessageDialog messageDialog;

	@Autowired
	private ProgressDialog progressDialog;

	private BillableType type;

	@Autowired
	private RestServerService serverService;

	@Override
	public Startable addParent(Stage stage) {
		return null;
	}

	@Override
	public void refresh() {
	}

	@Override
	public void setFocus() {
	}

	@Override
	public void start() {
		if (serverService.isOffSite() //
				&& scriptService.unpostedTransactionsExist()//
				&& type == DOWNLOAD)
			showPostOrReplicateDialog();
		else
			startTask(type().toString().toLowerCase());
	}

	@Override
	public BillableType type() {
		return type;
	}

	@Override
	public Startable type(BillableType type) {
		this.type = type;
		return this;
	}

	private void onFailed(Task<String> t) {
		progressDialog.close();
		String msg = t.getException().getMessage();
		messageDialog.showError(msg).addParent(this).start();
	}

	private void onSucceeded(Task<String> t) {
		try {
			progressDialog.close();
			String msg = t.get();
			if (startsWith(msg, "No"))
				messageDialog.showError(msg).addParent(this).start();
			else
				messageDialog.showInfo(msg).addParent(this).start();
		} catch (Exception e) {
			e.printStackTrace();
			messageDialog.show(e).addParent(this).start();
		}
	}

	private void postUnpostedTransaction() {
		messageDialog.close();
		startTask("upload");
	}

	private void replicate() {
		messageDialog.close();
		startTask(type().toString().toLowerCase());
	}

	private void showPostOrReplicateDialog() {
		messageDialog.showOption("Unposted transactions exist;\nproceed, how?", "Post", "Replicate");
		messageDialog.setOnOptionSelection(e -> postUnpostedTransaction());
		messageDialog.setOnDefaultSelection(e -> replicate());
		messageDialog.addParent(this).start();
	}

	private void start(Task<String> task) {
		Thread t = new Thread(task);
		t.setDaemon(true);
		t.start();
	}

	private void startTask(String type) {
		Task<String> task = newTask(type);
		task.setOnSucceeded(e -> onSucceeded(task));
		task.setOnFailed(e -> onFailed(task));
		progressDialog.addParent(this).start();
		start(task);
	}

	private Task<String> newTask(String type) {
		return new Task<String>() {
			@Override
			protected String call() throws Exception {
				return readOnlyService.module("sync").getOne("/" + type);
			}
		};
	}
}
