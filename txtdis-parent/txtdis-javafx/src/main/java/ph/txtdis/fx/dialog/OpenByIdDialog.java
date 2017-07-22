package ph.txtdis.fx.dialog;

import static ph.txtdis.type.Type.ID;
import static ph.txtdis.type.Type.TEXT;

import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javafx.scene.Node;
import javafx.scene.control.Button;
import ph.txtdis.fx.control.AppButtonImpl;
import ph.txtdis.fx.control.AppFieldImpl;
import ph.txtdis.fx.control.LabelFactory;
import ph.txtdis.fx.pane.AppGridPane;
import ph.txtdis.type.Type;

@Scope("prototype")
@Component("openByIdDialog")
public class OpenByIdDialog<PK> //
		extends AbstractInputDialog {

	@Autowired
	private AppGridPane grid;

	@Autowired
	private AppButtonImpl openButton;

	@Autowired
	private LabelFactory label;

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

	private Type getType() {
		return key instanceof Long ? ID : TEXT;
	}

	private Button openButton() {
		openButton.large("Open").build();
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
	protected Button[] buttons() {
		return new Button[] { openButton(), closeButton() };
	}

	@Override
	protected List<Node> nodes() {
		grid.getChildren().clear();
		grid.add(label.help(prompt), 0, 0, 2, 1);
		grid.add(label.field(labelName), 0, 1);
		grid.add(keyField.build(getType()), 1, 1);
		return Arrays.asList(header(), grid, buttonBox());
	}

	@Override
	protected void nullData() {
		super.nullData();
		key = null;
	}
}
