package ph.txtdis.fx.control;

import static javafx.scene.input.KeyCode.ENTER;
import static javafx.scene.input.KeyEvent.KEY_PRESSED;
import static ph.txtdis.type.Type.TEXT;
import static ph.txtdis.util.TypeStyle.align;
import static ph.txtdis.util.TypeStyle.parse;
import static ph.txtdis.util.TypeStyle.style;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.sun.javafx.scene.control.skin.TextFieldSkin;

import javafx.beans.binding.BooleanBinding;
import javafx.beans.value.ObservableBooleanValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import ph.txtdis.type.Type;
import ph.txtdis.util.TypeStyle;

@Component
@Scope("prototype")
@SuppressWarnings("restriction")
public class AppFieldImpl<T> //
	extends TextField //
	implements AppField<T> {

	private int length, width;

	private String prompt;

	private Type type;

	public AppFieldImpl() {
		setStyle("-fx-opacity: 1; ");
		traverseOnPressedEnterKey();
		cancelEditOnLostFocus();
	}

	private void traverseOnPressedEnterKey() {
		addEventFilter(KEY_PRESSED, e -> traverseOnPressedEnterKey(e));
	}

	private void cancelEditOnLostFocus() {
		focusedProperty().addListener((focus, outOfFocus, inFocus) -> cancelEditOnLostFocus(inFocus));
	}

	private void traverseOnPressedEnterKey(KeyEvent e) {
		if (e.getCode() == ENTER)
			((TextFieldSkin) getSkin()).getBehavior().traverseNext();
	}

	private void cancelEditOnLostFocus(Boolean inFocus) {
		if (!inFocus)
			cancelEdit();
	}

	@Override
	public AppFieldImpl<T> build(Type type) {
		this.type = type;
		setAlignment();
		setFieldWidth(width());
		validate();
		setPromptText(prompt);
		return this;
	}

	private void setAlignment() {
		align(type, this);
	}

	@Override
	public void setFieldWidth(int width) {
		setMinWidth(width);
		setPrefWidth(width);
		if (type != TEXT)
			setMaxWidth(width);
	}

	private int width() {
		return width != 0 ? width : TypeStyle.width(type);
	}

	private void validate() {
		if (!isDisabled())
			TypeStyle.validate(type, this);
	}

	@Override
	public void disable() {
		disableProperty().unbind();
		disableProperty().set(true);
	}

	@Override
	public void disableIf(ObservableBooleanValue b) {
		disableProperty().unbind();
		disableProperty().bind(b);
	}

	@Override
	public void enable() {
		disableProperty().unbind();
		disableProperty().set(false);
	}

	@Override
	public T getValue() {
		return parse(type, getText());
	}

	@Override
	public void setValue(T value) {
		style(type, this, value);
	}

	@Override
	public void handleError() {
		clear();
		requestFocus();
	}

	@Override
	public BooleanBinding isNot(String text) {
		return textProperty().isNotEqualTo(text);
	}

	@Override
	public BooleanBinding isNotEmpty() {
		return isEmpty().not();
	}

	@Override
	public BooleanBinding isEmpty() {
		return textProperty().isEmpty();
	}

	@Override
	public int length() {
		return length;
	}

	@Override
	public AppField<T> length(int length) {
		this.length = length;
		return this;
	}

	@Override
	public void onAction(EventHandler<ActionEvent> e) {
		setOnAction(e);
	}

	@Override
	public AppField<T> prompt(String prompt) {
		this.prompt = prompt;
		return this;
	}

	@Override
	public AppField<T> readOnly() {
		disableProperty().unbind();
		disableProperty().set(true);
		return this;
	}

	@Override
	public void showIf(ObservableBooleanValue b) {
		visibleProperty().unbind();
		visibleProperty().bind(b);
	}

	@Override
	public AppFieldImpl<T> width(int width) {
		this.width = width;
		return this;
	}
}
