package ph.txtdis.fx.dialog;

import static ph.txtdis.type.Type.TEXT;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import ph.txtdis.fx.control.AppButton;
import ph.txtdis.fx.control.AppFieldImpl;
import ph.txtdis.fx.control.LabelFactory;
import ph.txtdis.fx.control.PasswordInput;
import ph.txtdis.fx.pane.PaneFactory;
import ph.txtdis.fx.pane.AppGridPane;
import ph.txtdis.service.LoginService;
import ph.txtdis.service.RestServerService;
import ph.txtdis.service.SyncService;
import ph.txtdis.util.FontIcon;

@Scope("prototype")
@Component("loginDialog")
public class LoginDialog
	extends Stage {

	private static final String STYLE = "-fx-font-size: 11pt; -fx-base: #6a5acd; -fx-accent: -fx-base;"
		+ " -fx-focus-color: white; -fx-faint-focus-color: #ffffff22; ";

	@Autowired
	private PaneFactory pane;

	@Autowired
	private AppGridPane grid;

	@Autowired
	private AppButton loginButton, passwordButton, serverButton;

	@Autowired
	private AppFieldImpl<String> userField;

	@Autowired
	private LabelFactory label;

	@Autowired
	private PasswordInput passwordField;

	@Autowired
	private ServerSelectionDialog serverDialog;

	@Autowired
	private ChangePasswordDialog passwordDialog;

	@Autowired
	private MainMenu mainMenu;

	@Autowired
	private MessageDialog dialog;

	@Autowired
	private LoginService login;

	@Autowired
	private RestServerService serverService;

	@Autowired
	private SyncService syncService;

	private BooleanProperty ready = new SimpleBooleanProperty(false);

	@Override
	public void showAndWait() {
		setScene();
		clearFields();
		userField.requestFocus();
		ready.setValue(Boolean.TRUE);
		super.showAndWait();
	}

	private void setScene() {
		getIcons().add(new FontIcon("\ue945"));
		setTitle();
		initModality(Modality.APPLICATION_MODAL);
		setScene(scene());
	}

	private void clearFields() {
		userField.clear();
		passwordField.clear();
		userField.requestFocus();
	}

	private void setTitle() {
		setTitle("Welcome to txtDIS@" + serverService.getLocation() + "!");
	}

	private Scene scene() {
		VBox vb = pane.vertical(gridPane(), buttons());
		vb.setAlignment(Pos.CENTER);
		vb.setPadding(new Insets(20));
		vb.setStyle(STYLE);
		return new Scene(vb);
	}

	private Node gridPane() {
		grid.getChildren().clear();
		grid.add(label.field("Username"), 0, 0);
		grid.add(userField.build(TEXT), 1, 0);
		grid.add(label.field("Password"), 0, 1);
		grid.add(passwordField(), 1, 1);
		return grid;
	}

	private Node buttons() {
		HBox hb = pane.horizontal(loginButton(), passwordButton(), serverButton());
		hb.setAlignment(Pos.CENTER);
		return hb;
	}

	private Node passwordField() {
		passwordField.disableProperty().bind(userField.isEmpty());
		return passwordField;
	}

	private Node loginButton() {
		loginButton.text("Log-in").build();
		loginButton.disableIf(passwordField.isEmpty());
		loginButton.onAction(event -> tryLoggingInUponVerification());
		return loginButton;
	}

	private Node passwordButton() {
		passwordButton.text("Alter Password").build();
		passwordButton.disableIf(passwordField.isEmpty());
		passwordButton.onAction(event -> tryChangingPasswordUponVerification());
		return passwordButton;
	}

	private Node serverButton() {
		serverButton.text("Change Server").build();
		serverButton.onAction(event -> changeServer());
		return serverButton;
	}

	private void tryLoggingInUponVerification() {
		try {
			logInIfAuthenticated();
		} catch (Exception e) {
			closeOnError(e);
		}
	}

	private void tryChangingPasswordUponVerification() {
		try {
			changePasswordIfAuthenticated();
		} catch (Exception e) {
			closeOnError(e);
		}
	}

	private void changeServer() {
		serverDialog.updateStyle(STYLE).addParent(this).start();
		setTitle();
		clearFields();
	}

	private void logInIfAuthenticated() throws Exception {
		validate();
		validateVersionIsLatest_AndServerAndClientDatesAreInSync();
	}

	private void closeOnError(Exception e) {
		dialog.show(e).updateStyle(STYLE).addParent(this).start();
		Platform.exit();
	}

	private void changePasswordIfAuthenticated() throws Exception {
		validate();
		changePassword();
		clearFields();
	}

	private void validate() throws Exception {
		login.validate(userField.getText(), passwordField.getText());
	}

	private void validateVersionIsLatest_AndServerAndClientDatesAreInSync() {
		try {
			syncService.validateVersionIsLatest();
			syncService.validateServerAndClientDatesAreInSync();
			close();
			mainMenu.display();
		} catch (Exception e) {
			closeOnError(e);
		}
	}

	private void changePassword() {
		passwordDialog.updateStyle(STYLE).addParent(this).start();
	}
}
