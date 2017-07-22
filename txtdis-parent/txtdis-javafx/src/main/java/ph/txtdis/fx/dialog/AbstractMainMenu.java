package ph.txtdis.fx.dialog;

import javafx.application.Platform;
import javafx.stage.WindowEvent;
import ph.txtdis.util.FontIcon;

public abstract class AbstractMainMenu //
		extends AbstractMenu //
		implements MainMenu {

	@Override
	public void display() {
		getIcons().add(new FontIcon("\ue945"));
		setTitle(credentialService.username() + "@" + modulePrefix + " Main Menu");
		setScene(createScene());
		styleSheet.update(credentialService.user().getStyle());
		setOnCloseRequest(e -> onCloseRequestAction(e));
		show();
	}

	protected void onCloseRequestAction(WindowEvent e) {
		Platform.exit();
	}
}
