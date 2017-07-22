package ph.txtdis.fx.tab;

import javafx.beans.value.ObservableBooleanValue;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.scene.control.Tab;

public interface InputTab {

	Tab asTab();

	InputTab build();

	String getId();

	void refresh();

	void save();

	void select();

	void setOnSelectionChanged(EventHandler<Event> e);

	void disableIf(ObservableBooleanValue b);

	void clear();
}
