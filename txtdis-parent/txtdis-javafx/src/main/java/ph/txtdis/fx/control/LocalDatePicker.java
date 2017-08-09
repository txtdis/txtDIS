package ph.txtdis.fx.control;

import java.time.LocalDate;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.sun.javafx.scene.control.behavior.TextFieldBehavior;
import com.sun.javafx.scene.control.skin.TextFieldSkin;

import javafx.beans.binding.BooleanBinding;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.value.ObservableBooleanValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import ph.txtdis.fx.validator.DateInputValidator;

@Component
@Scope("prototype")
@SuppressWarnings("restriction")
public class LocalDatePicker //
	extends DatePicker //
	implements FocusRequested,
	InputControl<LocalDate> {

	public LocalDatePicker() {
		setStyle(" -fx-opacity: 1.0;");
		getEditor().setStyle(" -fx-opacity: 1.0;");
		setPickerWidth(140);
		traverseOnPressedEnterKey();
		setPromptText("08/08/2008");
		getEditor().textProperty().addListener(new DateInputValidator(this));
	}

	private void setPickerWidth(double w) {
		setMinWidth(w);
		setPrefWidth(w);
		setMaxWidth(w);
	}

	private void traverseOnPressedEnterKey() {
		addEventFilter(KeyEvent.KEY_PRESSED, event -> {
			if (event.getCode() == KeyCode.ENTER) {
				TextField textField = getEditor();
				TextFieldBehavior behavior = ((TextFieldSkin) textField.getSkin()).getBehavior();
				behavior.traverseNext();
			}
		});
	}

	@Override
	public void clear() {
		setValue(null);
	}

	public void disable() {
		disableProperty().unbind();
		disableProperty().set(true);
	}

	public ReadOnlyBooleanProperty disabled() {
		return disabledProperty();
	}

	public void disableIf(ObservableBooleanValue b) {
		disableProperty().bind(b);
	}

	public void editableIf(ObservableBooleanValue b) {
		editableProperty().bind(b);
		focusTraversableProperty().bind(editableProperty());
	}

	public void enable() {
		disableProperty().unbind();
		disableProperty().set(false);
	}

	public BooleanBinding isEmpty() {
		return editorProperty().get().textProperty().isEmpty();
	}

	public BooleanBinding isNotVisible() {
		return visibleProperty().not();
	}

	public void onAction(EventHandler<ActionEvent> e) {
		setOnAction(e);
	}

	public void showIf(ObservableBooleanValue b) {
		visibleProperty().unbind();
		visibleProperty().bind(b);
	}
}
