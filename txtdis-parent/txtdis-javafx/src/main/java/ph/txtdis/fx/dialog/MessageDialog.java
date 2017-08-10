package ph.txtdis.fx.dialog;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import ph.txtdis.fx.control.AppButton;
import ph.txtdis.info.Information;

import java.util.ArrayList;
import java.util.List;

import static java.util.Collections.singletonList;

@Scope("prototype")
@Component("messageDialog")
public class MessageDialog
	extends AbstractDialog {

	private boolean withOption;

	private AppButton closeButton, optionButton;

	private String text, unicode, closeText, color, optionText;

	@Override
	public void refresh() {
		goToDefaultFocus();
	}

	@Override
	public void goToDefaultFocus() {
		if (withOption)
			optionButton.requestFocus();
		else
			closeButton.requestFocus();
	}

	public MessageDialog setOnDefaultSelection(EventHandler<ActionEvent> e) {
		if (closeButton != null)
			closeButton.onAction(e);
		return this;
	}

	public MessageDialog setOnOptionSelection(EventHandler<ActionEvent> e) {
		if (optionButton != null)
			optionButton.onAction(e);
		return this;
	}

	public MessageDialog show(Exception e) {
		return showError(e.getMessage());
	}

	public MessageDialog showError(String error) {
		text = error;
		withOption = false;
		unicode = "\ue947";
		color = "maroon";
		closeText = "OK";
		return this;
	}

	public MessageDialog show(Information i) {
		return showInfo(i.getMessage());
	}

	public MessageDialog showInfo(String info) {
		text = info;
		withOption = false;
		unicode = "\ue92d";
		color = "lime";
		closeText = "OK";
		return this;
	}

	public MessageDialog showOption(String text, String option, String close) {
		this.text = text;
		this.optionText = option;
		this.closeText = close;
		this.withOption = true;
		unicode = "\ue95c";
		color = "yellow";
		return this;
	}

	@Override
	protected List<Node> nodes() {
		HBox buttonBox = pane.forMessageDialogButtons(buttons());
		HBox messageAndButtonBox = pane.forDialogMessages(label.message(text), buttonBox);
		HBox iconAndMessageAndButton = pane.horizontal(label.icon(unicode, color), messageAndButtonBox);
		return singletonList(iconAndMessageAndButton);
	}

	public List<Button> buttons() {
		List<Button> l = new ArrayList<>();
		if (withOption)
			l.add(optionButton());
		l.add(closeButton());
		return l;
	}

	private Button optionButton() {
		return optionButton = button.large(optionText).build();
	}

	private Button closeButton() {
		closeButton = button.large(closeText).build();
		if (!withOption)
			closeButton.onAction(e -> close());
		return closeButton;
	}
}
