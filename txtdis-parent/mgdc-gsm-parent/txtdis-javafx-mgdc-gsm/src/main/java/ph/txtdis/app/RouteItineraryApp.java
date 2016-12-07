package ph.txtdis.app;

import javafx.stage.Stage;
import ph.txtdis.fx.control.AppButton;
import ph.txtdis.service.ItineraryService;

public interface RouteItineraryApp {

	AppButton addButton(Stage stage, ItineraryService service);
}