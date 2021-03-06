package ph.txtdis.fx.control;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.sun.javafx.scene.control.behavior.PasswordFieldBehavior;
import com.sun.javafx.scene.control.skin.TextFieldSkin;

import javafx.beans.binding.BooleanBinding;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.scene.control.PasswordField;
import javafx.scene.input.InputMethodEvent;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

@Scope("prototype")
@Component
@SuppressWarnings("restriction")
public class PasswordInput
	extends PasswordField {

	public PasswordInput() {
		addEventFilter(KeyEvent.KEY_PRESSED, event -> {
			if (event.getCode() == KeyCode.ENTER) {
				TextFieldSkin skin = (TextFieldSkin) getSkin();
				PasswordFieldBehavior behavior = (PasswordFieldBehavior) skin.getBehavior();
				behavior.traverseNext();
				event.consume();
			}
		});
	}

	public void disableIf(ObservableValue<Boolean> observable) {
		disableProperty().bind(observable);
	}

	public BooleanBinding isEmpty() {
		return textProperty().isEmpty();
	}

	public void onAction(EventHandler<InputMethodEvent> e) {
		setOnInputMethodTextChanged(e);
	}
}
