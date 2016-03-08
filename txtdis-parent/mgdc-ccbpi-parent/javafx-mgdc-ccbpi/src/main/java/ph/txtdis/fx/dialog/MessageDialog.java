package ph.txtdis.fx.dialog;

import static java.util.Arrays.asList;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import ph.txtdis.fx.control.AppButton;
import ph.txtdis.fx.control.LabelFactory;
import ph.txtdis.fx.pane.DialogMessageBox;
import ph.txtdis.fx.pane.MessageDialogButtonBox;
import ph.txtdis.info.Information;

@Scope("prototype")
@Component("messageDialog")
public class MessageDialog extends AbstractDialog {

	@Autowired
	private AppButton closeButton, optionButton;

	@Autowired
	private LabelFactory label;

	@Autowired
	private MessageDialogButtonBox buttonBox;

	@Autowired
	private DialogMessageBox messageBox;

	private String text, unicode, color, option;

	private String close = "OK";

	private boolean withOption;

	public List<Button> buttons() {
		List<Button> l = new ArrayList<>();
		if (withOption)
			l.add(optionButton());
		l.add(closeButton());
		return l;
	}

	@Override
	public void refresh() {
		setFocus();
	}

	public void setErrorStyle() {
		unicode = "\ue80f";
		color = "maroon";
	}

	@Override
	public void setFocus() {
		closeButton.requestFocus();
	}

	public void setOnAction(EventHandler<ActionEvent> e) {
		optionButton.setOnAction(e);
	}

	public MessageDialog show(Exception e) {
		return showError(e.getMessage());
	}

	public MessageDialog show(Information i) {
		return showInfo(i.getMessage());
	}

	public MessageDialog showError(String error) {
		text = error;
		setErrorStyle();
		withOption = false;
		return this;
	}

	public MessageDialog showInfo(String info) {
		text = info;
		unicode = "\ue813";
		color = "lime";
		withOption = false;
		return this;
	}

	public MessageDialog showOption(String text, String option, String close) {
		this.text = text;
		this.option = option;
		this.close = close;
		this.withOption = true;
		setErrorStyle();
		return this;
	}

	private Button closeButton() {
		closeButton.large(close).build();
		closeButton.setOnAction(e -> close());
		return closeButton;
	}

	private Button optionButton() {
		return optionButton.large(option).build();
	}

	@Override
	protected List<Node> nodes() {
		buttonBox.addButtons(buttons());
		messageBox.addNodes(label.message(text), buttonBox);
		return asList(new HBox(label.icon(unicode, color), messageBox));
	}
}
