package ph.txtdis.fx.control;

import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;

public interface AppButton {

	AppButtonImpl build();

	AppButton color(String color);

	void disable();

	void disableIf(ObservableValue<Boolean> b);

	void enable();

	AppButton fontSize(int size);

	AppButton icon(String unicode);

	AppButton large(String text);

	void onAction(EventHandler<ActionEvent> e);

	void requestFocus();

	AppButton text(String text);

	AppButton tooltip(String tooltip);
}