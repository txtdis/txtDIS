package ph.txtdis.fx.dialog;

import javafx.application.Platform;
import javafx.stage.WindowEvent;
import ph.txtdis.util.FontIcon;
import ph.txtdis.util.UserUtils;

import static ph.txtdis.util.UserUtils.*;

public abstract class AbstractMainMenu //
	extends AbstractMenu //
	implements MainMenu {

	@Override
	public void display() {
		getIcons().add(new FontIcon("\ue945"));
		setTitle(username() + "@" + modulePrefix + " Main Menu");
		setScene(createScene());
		styleSheet.update(user().getStyle());
		setOnCloseRequest(e -> onCloseRequestAction(e));
		show();
	}

	protected void onCloseRequestAction(WindowEvent e) {
		Platform.exit();
	}
}
