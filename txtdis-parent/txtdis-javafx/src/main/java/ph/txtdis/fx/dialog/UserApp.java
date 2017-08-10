package ph.txtdis.fx.dialog;

import javafx.scene.Node;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import ph.txtdis.app.App;
import ph.txtdis.exception.NotFoundException;
import ph.txtdis.fx.control.AppButton;
import ph.txtdis.fx.control.AppCheckBox;
import ph.txtdis.fx.control.AppFieldImpl;
import ph.txtdis.fx.pane.AppGridPane;
import ph.txtdis.fx.table.RoleTable;
import ph.txtdis.info.Information;
import ph.txtdis.service.UserService;

import java.util.List;

import static java.util.Arrays.asList;
import static ph.txtdis.type.Type.TEXT;

@Scope("prototype")
@Component("userApp")
public class UserApp
	extends AbstractInputDialog
	implements App {

	@Autowired
	private AppFieldImpl<String> userField, surnameField;

	@Autowired
	private AppGridPane grid;

	@Autowired
	private AppCheckBox enabledCheckbox;

	@Autowired
	private RoleTable roleTable;

	@Autowired
	private UserService service;

	private AppButton saveButton;

	@Override
	protected List<AppButton> buttons() {
		return asList(saveButton(), closeButton());
	}

	private AppButton saveButton() {
		saveButton = button.large("Save").build();
		saveButton.onAction(e -> save());
		return saveButton;
	}

	private void save() {
		try {
			service.save(roleTable.getItems());
		} catch (Information i) {
			showInfoDialog(i);
		} catch (Exception e) {
			showErrorDialog(e);
		} finally {
			refresh();
		}
	}

	@Override
	public void refresh() {
		userField.setValue(service.getUsername());
		surnameField.setValue(service.getSurname());
		enabledCheckbox.setValue(service.isEnabled());
		roleTable.items(service.getRolesThatCanBeAssigned());
		super.refresh();
	}

	@Override
	protected String headerText() {
		return "Input User Details";
	}

	@Override
	protected List<Node> nodes() {
		saveButton.disableIf(roleTable.isEmpty());
		return asList(header(), grid(), tablePane(), buttonBox());
	}

	private AppGridPane grid() {
		grid.getChildren().clear();
		grid.add(label.field("Username"), 0, 0);
		grid.add(userField(), 1, 0);
		grid.add(label.field("Surname"), 0, 1);
		grid.add(surnameField(), 1, 1);
		grid.add(label.field("Enabled"), 0, 2);
		grid.add(enabledCheckbox(), 1, 2);
		return grid;
	}

	private Node tablePane() {
		roleTable.build();
		roleTable.disableIf(enabledCheckbox.disableProperty());
		return pane.centeredHorizontal(roleTable);
	}

	private AppFieldImpl<String> userField() {
		userField.build(TEXT);
		userField.onAction(e -> validate());
		return userField;
	}

	private AppFieldImpl<String> surnameField() {
		surnameField.build(TEXT);
		surnameField.disableIf(userField.isEmpty());
		surnameField.onAction(e -> service.setSurname(surnameField.getText()));
		return surnameField;
	}

	private AppCheckBox enabledCheckbox() {
		enabledCheckbox.disableIf(surnameField.isEmpty());
		enabledCheckbox.onAction(e -> service.setEnabled(enabledCheckbox.getValue()));
		return enabledCheckbox;
	}

	private void validate() {
		if (userField.isNotEmpty().get())
			try {
				service.validateUsername(userField.getText());
				refresh();
			} catch (NotFoundException e) {
				surnameField.requestFocus();
			} catch (Exception e) {
				showErrorDialog(e);
				userField.clear();
				userField.requestFocus();
			}
		else
			userField.requestFocus();
	}

	@Override
	protected void setOnClickedCloseButton() {
		service.reset();
		close();
	}

	@Override
	public void goToDefaultFocus() {
		userField.requestFocus();
	}
}