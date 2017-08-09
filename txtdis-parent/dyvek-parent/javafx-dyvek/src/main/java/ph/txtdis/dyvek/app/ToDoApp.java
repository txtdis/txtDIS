package ph.txtdis.dyvek.app;

import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.event.EventType;
import javafx.stage.WindowEvent;
import ph.txtdis.app.LaunchableApp;

import static javafx.stage.WindowEvent.WINDOW_HIDDEN;

public interface ToDoApp
	extends LaunchableApp {

	default void removeOnHidden() {
		removeEventHandler(WINDOW_HIDDEN, getOnHidden());
	}

	<T extends Event> void removeEventHandler(EventType<T> t, EventHandler<? super T> h);

	EventHandler<WindowEvent> getOnHidden();

	void setOnHidden(EventHandler<WindowEvent> e);
}
