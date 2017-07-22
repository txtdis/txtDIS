package ph.txtdis.fx.dialog;

import org.springframework.beans.factory.annotation.Autowired;

import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import ph.txtdis.fx.control.AppButtonImpl;
import ph.txtdis.fx.control.LabelFactory;
import ph.txtdis.fx.pane.DialogButtonBox;

public abstract class AbstractInputDialog //
		extends AbstractDialog {

	@Autowired
	protected MessageDialog dialog;

	@Autowired
	protected AppButtonImpl closeButton;

	@Autowired
	protected LabelFactory label;

	@Autowired
	protected DialogButtonBox box;

	protected String heading, prompt;

	public AbstractInputDialog() {
		super();
		nullData();
	}

	protected void nullData() {
		heading = null;
		prompt = null;
	}

	public AbstractInputDialog header(String heading) {
		this.heading = heading;
		return this;
	}

	public AbstractInputDialog prompt(String prompt) {
		this.prompt = prompt;
		return this;
	}

	@Override
	public void refresh() {
		goToDefaultFocus();
	}

	protected Button closeButton(String name) {
		closeButton.large(name).build();
		closeButton.onAction(event -> setOnClickedCloseButton());
		return closeButton;
	}

	protected HBox buttonBox() {
		return box.addButtons(buttons());
	}

	protected Button[] buttons() {
		return new Button[] { closeButton() };
	}

	protected Button closeButton() {
		return closeButton("Close");
	}

	protected Label header() {
		return label.dialog(headerText());
	}

	protected String headerText() {
		return heading;
	}

	protected void setOnClickedCloseButton() {
		nullData();
		refresh();
		close();
	}

	protected void resetNodesOnError(Exception e) {
		e.printStackTrace();
		dialog.show(e).addParent(this).start();
		refresh();
	}
}
