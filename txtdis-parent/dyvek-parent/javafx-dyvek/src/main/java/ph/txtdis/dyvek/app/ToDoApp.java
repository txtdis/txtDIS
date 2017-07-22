package ph.txtdis.dyvek.app;

import static javafx.stage.WindowEvent.WINDOW_HIDDEN;

import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.event.EventType;
import javafx.stage.WindowEvent;
import ph.txtdis.app.LaunchableApp;

public interface ToDoApp //
		extends LaunchableApp {

	void setOnHidden(EventHandler<WindowEvent> e);

	EventHandler<WindowEvent> getOnHidden();

	default void removeOnHidden() {
		removeEventHandler(WINDOW_HIDDEN, getOnHidden());
	}

	<T extends Event> void removeEventHandler(EventType<T> t, EventHandler<? super T> h);
}
