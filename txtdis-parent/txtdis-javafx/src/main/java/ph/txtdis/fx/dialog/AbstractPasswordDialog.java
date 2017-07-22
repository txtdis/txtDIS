package ph.txtdis.fx.dialog;

import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import javafx.scene.Node;
import javafx.scene.control.Button;
import ph.txtdis.dto.User;
import ph.txtdis.exception.DifferentPasswordException;
import ph.txtdis.fx.control.AppButtonImpl;
import ph.txtdis.fx.control.LabelFactory;
import ph.txtdis.fx.control.PasswordInput;
import ph.txtdis.fx.pane.AppGridPane;
import ph.txtdis.info.Information;
import ph.txtdis.info.SuccessfulSaveInfo;
import ph.txtdis.service.CredentialService;
import ph.txtdis.service.UserService;

public abstract class AbstractPasswordDialog //
		extends AbstractInputDialog {

	@Autowired
	private CredentialService credentialService;

	@Autowired
	protected UserService userService;

	@Autowired
	private LabelFactory label;

	@Autowired
	protected PasswordInput password1, password2;

	@Autowired
	protected AppButtonImpl changeButton;

	@Autowired
	protected AppGridPane grid;

	@Override
	public void goToDefaultFocus() {
		password1.requestFocus();
	}

	@Override
	protected Button[] buttons() {
		return new Button[] { changeButton(), closeButton() };
	}

	protected Button changeButton() {
		changeButton.large("Change").build();
		changeButton.onAction(event -> saveIfPasswordsAreTheSame());
		changeButton.disableIf(password2.isEmpty());
		return changeButton;
	}

	protected String encodedPassword() {
		return credentialService.encode(password2());
	}

	protected AppGridPane grid() {
		grid.add(label.field("Enter Password"), 0, 1);
		grid.add(password1, 1, 1);
		grid.add(label.field("Re-Type Password"), 0, 2);
		grid.add(password2, 1, 2);
		return grid;
	}

	@Override
	protected List<Node> nodes() {
		setBindings();
		return Arrays.asList(header(), grid(), buttonBox());
	}

	protected String password2() {
		return password2.getText();
	}

	protected void saveUser() throws SuccessfulSaveInfo, Exception {
		User user = credentialService.user();
		user.setPassword(encodedPassword());
		userService.save(user);
	}

	protected void setBindings() {
		password2.disableIf(password1.isEmpty());
		changeButton.disableIf(password2.isEmpty());
	}

	private void comparePasswords() throws Exception {
		if (!(password1().equals(password2())))
			throw new DifferentPasswordException();
	}

	private void handleError(Exception e) {
		dialog.show(e).addParent(this).start();
		refresh();
	}

	private String password1() {
		return password1.getText();
	}

	private void save() throws SuccessfulSaveInfo, Exception {
		saveUser();
		credentialService.setPassword(password2());
		close();
	}

	private void saveIfPasswordsAreTheSame() {
		try {
			comparePasswords();
			save();
		} catch (Exception | Information e) {
			handleError((Exception) e);
		}
	}

	@Override
	public void refresh() {
		password1.clear();
		password2.clear();
		super.refresh();
	}
}