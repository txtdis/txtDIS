package ph.txtdis.app;

import javafx.stage.Stage;

public interface App {

	App addParent(Stage stage);

	void goToDefaultFocus();

	void initialize();

	void refresh();

	void start();
}