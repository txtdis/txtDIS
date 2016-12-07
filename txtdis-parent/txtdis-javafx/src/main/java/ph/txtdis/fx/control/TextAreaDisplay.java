package ph.txtdis.fx.control;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javafx.beans.binding.BooleanBinding;
import javafx.scene.Node;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;

@Scope("prototype")
@Component("textAreaDisplay")
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

	public ScrollPane get() {
		return scrollPane;
	}

	private Node textArea() {
		textArea = new TextArea();
		textArea.setWrapText(true);
		textArea.setPrefRowCount(4);
		textArea.editableProperty().set(false);
		textArea.focusTraversableProperty().set(false);
		if (width > 0)
			textArea.setMaxWidth(width);
		return textArea;
	}

	public BooleanBinding isEmpty() {
		return textArea.textProperty().isEmpty();
	}

	public BooleanBinding isNotEmpty() {
		return isEmpty().not();
	}

	public void setValue(String value) {
		textArea.setText(value);
	}

	public BooleanBinding is(String text) {
		return textArea.textProperty().isEqualTo(text.trim());
	}

	public BooleanBinding isNot(String text) {
		return is(text).not();
	}

	public TextAreaDisplay editableIf(BooleanBinding bb) {
		boolean b = bb.get();
		return setEditable(b);
	}

	private TextAreaDisplay setEditable(boolean b) {
		textArea.setEditable(b);
		textArea.setFocusTraversable(b);
		return this;
	}

	public TextAreaDisplay editable() {
		return setEditable(true);
	}

	public TextAreaDisplay width(int width) {
		this.width = width;
		return this;
	}

	public String getValue() {
		return textArea.getText();
	}
}
