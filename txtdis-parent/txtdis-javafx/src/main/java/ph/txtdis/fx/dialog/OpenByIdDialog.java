package ph.txtdis.fx.dialog;

import javafx.scene.Node;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import ph.txtdis.fx.control.AppButton;
import ph.txtdis.fx.control.AppFieldImpl;
import ph.txtdis.fx.pane.AppGridPane;
import ph.txtdis.type.Type;

import java.util.List;

import static java.util.Arrays.asList;
import static ph.txtdis.type.Type.ID;
import static ph.txtdis.type.Type.TEXT;

@Scope("prototype")
@Component("openByIdDialog")
public class OpenByIdDialog<PK> //
	extends AbstractInputDialog {

	@Autowired
	private AppGridPane grid;

	@Autowired
	private AppFieldImpl<PK> keyField;

	private String labelName = "ID No";

	private PK key;

	public OpenByIdDialog<PK> idPrompt(String labelName) {
		this.labelName = labelName;
		return this;
	}

	public String getKey() {
		return key == null ? null : key.toString();
	}

	@Override
	public void goToDefaultFocus() {
		keyField.requestFocus();
	}

	@Override
	protected List<AppButton> buttons() {
		return asList(openButton(), closeButton());
	}

	private AppButton openButton() {
		AppButton openButton = button.large("Open").build();
		openButton.onAction(event -> setEnteredId());
		openButton.disableIf(keyField.isEmpty());
		return openButton;
	}

	private void setEnteredId() {
		key = keyField.getValue();
		keyField.setValue(null);
		close();
	}

	@Override
	protected List<Node> nodes() {
		grid.getChildren().clear();
		grid.add(label.help(prompt), 0, 0, 2, 1);
		grid.add(label.field(labelName), 0, 1);
		grid.add(keyField.build(getType()), 1, 1);
		return asList(header(), grid, buttonBox());
	}

	private Type getType() {
		return key instanceof Long ? ID : TEXT;
	}

	@Override
	protected void nullData() {
		super.nullData();
		key = null;
	}
}
