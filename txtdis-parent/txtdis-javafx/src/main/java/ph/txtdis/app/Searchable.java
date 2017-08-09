package ph.txtdis.app;

import javafx.stage.Stage;
import ph.txtdis.fx.dialog.MessageDialog;
import ph.txtdis.fx.dialog.SearchDialog;
import ph.txtdis.service.SearchedByNameService;

public interface Searchable<T> {

	default void openSearchDialog(SearchedByNameService<T> service,
	                              Stage stage,
	                              SelectableListApp<T> app,
	                              MessageDialog messageDialog,
	                              SearchDialog searchDialog) {
		searchDialog.criteria("name").addParent(stage).start();
		String name = searchDialog.getText();
		if (name != null)
			try {
				search(service, stage, app, name);
			} catch (Exception e) {
				messageDialog.show(e).addParent(stage).start();
			}
	}

	default void search(SearchedByNameService<T> service, Stage stage, SelectableListApp<T> app, String name) throws
		Exception {
		service.search(name);
		app.addParent(stage).start();
		T t = app.getSelection();
		if (t != null)
			updateUponVerification(t);
		nextFocus();
	}

	void updateUponVerification(T t) throws Exception;

	void nextFocus();
}
