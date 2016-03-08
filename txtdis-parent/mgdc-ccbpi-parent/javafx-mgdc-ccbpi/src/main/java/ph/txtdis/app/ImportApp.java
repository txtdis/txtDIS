package ph.txtdis.app;

import static org.apache.log4j.Logger.getLogger;

import java.io.File;
import java.util.concurrent.ExecutionException;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import javafx.concurrent.Task;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Stage;
import ph.txtdis.exception.DuplicateException;
import ph.txtdis.exception.NoItemPriceException;
import ph.txtdis.fx.dialog.ItemDialog;
import ph.txtdis.fx.dialog.MessageDialog;
import ph.txtdis.fx.dialog.ProgressDialog;
import ph.txtdis.info.SuccessfulSaveInfo;
import ph.txtdis.service.ImportService;

@Lazy
@Component("importApp")
public class ImportApp extends Stage implements Startable {

	private static Logger logger = getLogger(ImportApp.class);

	@Autowired
	private ImportService service;

	@Autowired
	private ItemDialog itemDialog;

	@Autowired
	private MessageDialog messageDialog;

	@Autowired
	private ProgressDialog progressDialog;

	@Override
	public Startable addParent(Stage stage) {
		return null;
	}

	public void importFile() {
		File selected = chooseFile().showOpenDialog(this);
		if (selected != null)
			importFile(selected);
	}

	@Override
	public void refresh() {
	}

	@Override
	public void setFocus() {
	}

	@Override
	public void start() {
		try {
			service.verifyNoImportDone();
			importFile();
		} catch (DuplicateException e) {
			e.printStackTrace();
			messageDialog.showError(e.getMessage() + "\nif needed, add via 'Assign' module").addParent(this).start();
		} catch (Exception e) {
			e.printStackTrace();
			messageDialog.show(e).addParent(this).start();
		}
	}

	private FileChooser chooseFile() {
		FileChooser fc = new FileChooser();
		fc.setTitle("Open RDL Summary");
		fc.getExtensionFilters().add(new ExtensionFilter("RDL Summary", "*magnum.xlsx"));
		return fc;
	}

	private void enterPriceIfNone(Task<Throwable> t, File f) {
		try {
			progressDialog.close();
			handleThrowable(t, f);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void handleThrowable(Task<Throwable> task, File f) throws InterruptedException, ExecutionException {
		Throwable t = task.get();
		if (t instanceof NoItemPriceException) {
			itemDialog.codeOf(t.getMessage()).addParent(this).start();
			importFile(f);
		} else if (t instanceof SuccessfulSaveInfo) {
			messageDialog.showInfo(t.getMessage()).addParent(this).start();
		} else {
			messageDialog.showError("Import unsuccessful").addParent(this).start();
		}
	}

	private void importFile(File selected) {
		Task<Throwable> task = new Task<Throwable>() {
			@Override
			protected Throwable call() throws Exception {
				try {
					service.importFile(selected);
					return null;
				} catch (SuccessfulSaveInfo i) {
					logger.info("Import completed successfully");
					return i;
				} catch (NoItemPriceException e) {
					logger.info("NoItemPriceException thrown");
					return e;
				} catch (Exception e) {
					logger.info("General exception thrown");
					return e;
				}
			}
		};

		task.setOnSucceeded(e -> enterPriceIfNone(task, selected));

		progressDialog.addParent(this).start();
		Thread t = new Thread(task);
		t.setDaemon(true);
		t.start();
	}
}
