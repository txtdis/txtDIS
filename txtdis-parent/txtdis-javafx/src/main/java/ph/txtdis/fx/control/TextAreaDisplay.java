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

	private TextArea textArea;

	private ScrollPane scrollPane;

	public ScrollPane build() {
		scrollPane = new ScrollPane(textArea());
		scrollPane.setFitToWidth(true);
		scrollPane.setFitToHeight(true);
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
}
