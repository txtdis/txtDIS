package ph.txtdis.fx.control;

import static javafx.scene.input.KeyCode.ENTER;
import static javafx.scene.input.KeyEvent.KEY_PRESSED;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.sun.javafx.scene.control.behavior.ButtonBehavior;
import com.sun.javafx.scene.control.skin.CheckBoxSkin;

import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.CheckBox;

@Scope("prototype")
@Component("appCheckBox")
@SuppressWarnings("restriction")
public class AppCheckBox //
	extends CheckBox //
	implements InputControl<Boolean> {

	public AppCheckBox() {
		traverseOnPressedEnterKey();
	}

	private void traverseOnPressedEnterKey() {
		addEventFilter(KEY_PRESSED, event -> {
			if (event.getCode() == ENTER) {
				CheckBoxSkin skin = (CheckBoxSkin) getSkin();
				ButtonBehavior<CheckBox> behavior = skin.getBehavior();
				behavior.traverseNext();
			}
		});
	}

	@Override
	public void clear() {
		setValue(null);
	}

	public void disableIf(ObservableValue<Boolean> b) {
		disableProperty().bind(b);
	}

	@Override
	public Boolean getValue() {
		return selectedProperty().get();
	}

	@Override
	public void setValue(Boolean b) {
		setSelected(b == null ? false : b);
	}

	public AppCheckBox label(String n) {
		setText(n);
		return this;
	}

	public void onAction(EventHandler<ActionEvent> e) {
		setOnAction(e);
	}

	public AppCheckBox readOnly() {
		disableProperty().unbind();
		disableProperty().set(true);
		return this;
	}
}
