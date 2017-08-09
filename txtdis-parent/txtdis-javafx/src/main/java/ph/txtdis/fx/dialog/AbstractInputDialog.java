package ph.txtdis.fx.dialog;

import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import org.springframework.beans.factory.annotation.Lookup;
import ph.txtdis.fx.control.AppButton;

import java.util.List;

import static java.util.Collections.singletonList;

public abstract class AbstractInputDialog
	extends AbstractDialog {

	protected AppButton closeButton;

	protected String prompt;

	private String heading;

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

	protected HBox buttonBox() {
		return pane.forDialogButtons(buttons());
	}

	protected List<AppButton> buttons() {
		return singletonList(closeButton());
	}

	protected AppButton closeButton() {
		return closeButton("Close");
	}

	protected AppButton closeButton(String name) {
		AppButton closeButton = button.large(name).build();
		closeButton.onAction(event -> setOnClickedCloseButton());
		return closeButton;
	}

	protected void setOnClickedCloseButton() {
		nullData();
		refresh();
		close();
	}

	@Override
	public void refresh() {
		goToDefaultFocus();
	}

	protected Label header() {
		return label.dialog(headerText());
	}

	protected String headerText() {
		return heading;
	}

	protected void resetNodesOnError(Exception e) {
		e.printStackTrace();
		messageDialog().show(e).addParent(this).start();
		refresh();
	}

	@Lookup
	protected MessageDialog messageDialog() {
		return null;
	}
}
