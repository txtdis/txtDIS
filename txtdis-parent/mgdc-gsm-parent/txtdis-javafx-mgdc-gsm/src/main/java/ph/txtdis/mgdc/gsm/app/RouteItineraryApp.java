package ph.txtdis.mgdc.gsm.app;

import javafx.stage.Stage;
import ph.txtdis.fx.control.AppButton;
import ph.txtdis.mgdc.gsm.service.ItineraryService;

public interface RouteItineraryApp {

	AppButton addButton(Stage stage, ItineraryService service);
}