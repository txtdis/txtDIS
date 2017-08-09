package ph.txtdis.fx.dialog;

import javafx.scene.Node;
import org.springframework.beans.factory.annotation.Autowired;
import ph.txtdis.dto.User;
import ph.txtdis.exception.DifferentPasswordException;
import ph.txtdis.fx.control.AppButton;
import ph.txtdis.fx.control.LabelFactory;
import ph.txtdis.fx.control.PasswordInput;
import ph.txtdis.fx.pane.AppGridPane;
import ph.txtdis.info.Information;
import ph.txtdis.info.SuccessfulSaveInfo;
import ph.txtdis.service.UserService;

import java.util.List;

import static java.util.Arrays.asList;
import static ph.txtdis.util.UserUtils.*;

public abstract class AbstractPasswordDialog //
	extends AbstractInputDialog {

	@Autowired
	protected UserService userService;

	@Autowired
	protected PasswordInput password1, password2;

	@Autowired
	protected AppGridPane grid;

	private AppButton changeButton;

	@Override
	public void goToDefaultFocus() {
		password1.requestFocus();
	}

	@Override
	protected List<AppButton> buttons() {
		return asList(changeButton(), closeButton());
	}

	private AppButton changeButton() {
		changeButton = button.large("Change").build();
		changeButton.onAction(event -> saveIfPasswordsAreTheSame());
		changeButton.disableIf(password2.isEmpty());
		return changeButton;
	}

	private void saveIfPasswordsAreTheSame() {
		try {
			comparePasswords();
			save();
		} catch (Information i) {
			i.printStackTrace();
		} catch (Exception e) {
			handleError(e);
		}
	}

	private void comparePasswords() throws Exception {
		if (!(password1().equals(password2())))
			throw new DifferentPasswordException();
	}

	private void save() throws SuccessfulSaveInfo, Exception {
		saveUser();
		setPassword(password2());
		close();
	}

	private void handleError(Exception e) {
		messageDialog().show(e).addParent(this).start();
		refresh();
	}

	private String password1() {
		return password1.getText();
	}

	private String password2() {
		return password2.getText();
	}

	private void saveUser() throws SuccessfulSaveInfo, Exception {
		User user = user();
		user.setPassword(encodedPassword());
		userService.save(user);
	}

	@Override
	public void refresh() {
		password1.clear();
		password2.clear();
		super.refresh();
	}

	private String encodedPassword() {
		return encode(password2());
	}

	@Override
	protected List<Node> nodes() {
		setBindings();
		return asList(header(), grid(), buttonBox());
	}

	protected void setBindings() {
		password2.disableIf(password1.isEmpty());
		changeButton.disableIf(password2.isEmpty());
	}

	protected AppGridPane grid() {
		grid.add(label.field("Enter Password"), 0, 1);
		grid.add(password1, 1, 1);
		grid.add(label.field("Re-Type Password"), 0, 2);
		grid.add(password2, 1, 2);
		return grid;
	}
}