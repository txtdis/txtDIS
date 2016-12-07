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
import ph.txtdis.fx.control.AppButton;
import ph.txtdis.fx.control.AppField;
import ph.txtdis.fx.control.LabelFactory;
import ph.txtdis.fx.pane.AppGridPane;
import ph.txtdis.type.Type;

@Scope("prototype")
@Component("openByIdDialog")
public class OpenByIdDialog<PK> extends AbstractInputDialog {

	@Autowired
	private AppGridPane grid;

	@Autowired
	private AppButton openButton;

	@Autowired
	private LabelFactory label;

	@Autowired
	private AppField<PK> idField;

	private String labelName = "ID No";

	private PK id;

	public OpenByIdDialog<PK> idPrompt(String labelName) {
		this.labelName = labelName;
		return this;
	}

	public String getId() {
		return id == null ? null : id.toString();
	}

	@Override
	public void setFocus() {
		idField.requestFocus();
	}

	private Type getType() {
		return id instanceof Long ? ID : TEXT;
	}

	private Button openButton() {
		openButton.large("Open").build();
		openButton.setOnAction(event -> setEnteredId());
		openButton.disableIf(idField.isEmpty());
		return openButton;
	}

	private void setEnteredId() {
		id = idField.getValue();
		idField.setValue(null);
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
		grid.add(idField.build(getType()), 1, 1);
		return Arrays.asList(header(), grid, buttonBox());
	}

	@Override
	protected void setOnFiredCloseButton() {
		id = null;
		super.setOnFiredCloseButton();
	}
}
