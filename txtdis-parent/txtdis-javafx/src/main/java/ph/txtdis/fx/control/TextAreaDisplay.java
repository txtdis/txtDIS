package ph.txtdis.fx.control;

import static javafx.scene.input.KeyCode.ENTER;
import static javafx.scene.input.KeyEvent.KEY_PRESSED;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.sun.javafx.scene.control.skin.TextAreaSkin;

import javafx.beans.binding.BooleanBinding;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.input.InputMethodEvent;
import javafx.scene.input.KeyEvent;

@Scope("prototype")
@Component("textAreaDisplay")
@SuppressWarnings("restriction")
public class TextAreaDisplay {

	private int width;

	private TextArea textArea;

	private ScrollPane scrollPane;

	public ScrollPane build() {
		scrollPane = new ScrollPane(textArea());
		scrollPane.setFitToWidth(true);
		scrollPane.setFitToHeight(true);
		if (width > 0)
			scrollPane.setMaxWidth(width);
		return scrollPane;
	}

	private Node textArea() {
		textArea = new TextArea();
		textArea.setWrapText(true);
		textArea.setPrefRowCount(3);
		textArea.setEditable(false);
		textArea.focusTraversableProperty().set(false);
		textArea.editableProperty().addListener(e -> changeEditProperties());
		if (width > 0)
			textArea.setMaxWidth(width);
		return textArea;
	}

	public BooleanBinding doesNotContain(String text) {
		boolean b = textArea.textProperty().getValue().contains(text);
		return new SimpleBooleanProperty(b).not();
	}

	public TextAreaDisplay editableIf(ObservableValue<Boolean> b) {
		textArea.editableProperty().unbind();
		textArea.editableProperty().bind(b);
		return this;
	}

	public TextAreaDisplay makeEditable() {
		textArea.setEditable(true);
		setEditableProperties();
		return this;
	}

	private void setEditableProperties() {
		textArea.setFocusTraversable(true);
		textArea.addEventFilter(KEY_PRESSED, e -> traverseOnPressedEnterKey(e));
		textArea.textProperty().addListener((text, oldText, newText) -> toUpperCase(newText));
	}

	public void requestFocus() {
		textArea.requestFocus();
	}

	private void changeEditProperties() {
		if (textArea.editableProperty().get())
			setEditableProperties();
	}

	private void traverseOnPressedEnterKey(KeyEvent e) {
		if (e.getCode() == ENTER)
			((TextAreaSkin) textArea.getSkin()).getBehavior().traverseNext();
	}

	private void toUpperCase(String text) {
		if (text != null)
			textArea.setText(text.toUpperCase());
	}

	public ScrollPane get() {
		return scrollPane;
	}

	public String getValue() {
		return textArea.textProperty().get();
	}

	public BooleanBinding is(String text) {
		return textArea.textProperty().isEqualTo(text.trim());
	}

	public BooleanProperty isEditable() {
		return textArea.editableProperty();
	}

	public BooleanBinding isEmpty() {
		return textArea.textProperty().isEmpty();
	}

	public BooleanBinding isNot(String text) {
		return is(text).not();
	}

	public BooleanBinding isNotEmpty() {
		return isEmpty().not();
	}

	public void onAction(EventHandler<InputMethodEvent> e) {
		textArea.setOnInputMethodTextChanged(e);
	}

	public void setValue(String value) {
		textArea.setText(value);
	}

	public TextAreaDisplay width(int width) {
		this.width = width;
		return this;
	}
}
