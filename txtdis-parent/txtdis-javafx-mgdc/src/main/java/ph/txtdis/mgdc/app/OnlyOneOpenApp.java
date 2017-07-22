package ph.txtdis.mgdc.app;

import javafx.event.EventHandler;
import javafx.stage.WindowEvent;

public interface OnlyOneOpenApp {

	void close();

	void setOnShowing(EventHandler<WindowEvent> e);
}
