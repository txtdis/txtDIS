package ph.txtdis.app;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javafx.stage.Stage;

@Scope("prototype")
@Component("dialogClosingOnlyApp")
public class DialogClosingOnlyApp //
		implements LaunchableApp {

	@Override
	public void actOn(String... id) {
	}

	@Override
	public StartableApp addParent(Stage stage) {
		return null;
	}

	@Override
	public void goToDefaultFocus() {
	}

	@Override
	public void initialize() {
	}

	@Override
	public boolean isDialogCloserOnly() {
		return true;
	}

	@Override
	public void refresh() {
	}

	@Override
	public void showAndWait() {
	}

	@Override
	public void start() {
	}

	@Override
	public void startAndWait() {
	}
}
