package ph.txtdis.app;

import javafx.stage.Stage;

public interface StartableApp {

	StartableApp addParent(Stage stage);

	void goToDefaultFocus();

	void initialize();

	void refresh();

	void start();
}