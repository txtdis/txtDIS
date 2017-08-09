package ph.txtdis.fx.control;

import javafx.beans.binding.BooleanBinding;
import javafx.beans.value.ObservableBooleanValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import ph.txtdis.type.Type;

public interface AppField<T> //
	extends ErrorHandling,
	FocusRequested,
	InputControl<T>,
	StylableTextField {

	AppFieldImpl<T> build(Type type);

	void disable();

	void disableIf(ObservableBooleanValue b);

	void enable();

	BooleanBinding isEmpty();

	BooleanBinding isNot(String text);

	BooleanBinding isNotEmpty();

	int length();

	AppField<T> length(int length);

	void onAction(EventHandler<ActionEvent> e);

	AppField<T> prompt(String prompt);

	AppField<T> readOnly();

	void setFieldWidth(int width);

	void showIf(ObservableBooleanValue b);

	AppField<T> width(int width);
}