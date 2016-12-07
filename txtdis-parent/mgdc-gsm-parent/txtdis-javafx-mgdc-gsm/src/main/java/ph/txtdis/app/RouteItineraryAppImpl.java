package ph.txtdis.app;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javafx.stage.Stage;
import ph.txtdis.dto.Customer;
import ph.txtdis.dto.Route;
import ph.txtdis.fx.control.AppButton;
import ph.txtdis.fx.dialog.ExTruckRouteItineraryDialog;
import ph.txtdis.fx.dialog.MessageDialog;
import ph.txtdis.fx.table.RouteItineraryTable;
import ph.txtdis.service.ItineraryService;

@Scope("prototype")
@Component("routeItineraryApp")
public class RouteItineraryAppImpl implements RouteItineraryApp {

	@Autowired
	private AppButton routeItineraryButton;

	@Autowired
	private ExTruckRouteItineraryDialog routeItineraryDialog;

	@Autowired
	private MessageDialog dialog;

	@Autowired
	private RouteItineraryTable routeItineraryTable;

	private ItineraryService service;

	private Stage stage;

	@Override
	public AppButton addButton(Stage stage, ItineraryService service) {
		this.service = service;
		routeItineraryButton.icon("route").tooltip("DRoAR...").build();
		routeItineraryButton.setOnAction(e -> generateRouteItinerary());
		return routeItineraryButton;
	}

	private void generateRouteItinerary() {
		Route r = getSelectedRoute();
		generateExcelRouteItinerary(service.listCustomersByScheduledRouteAndGoodCreditStanding(r));
	}

	private Route getSelectedRoute() {
		routeItineraryDialog.addParent(stage).start();
		return routeItineraryDialog.getRoute();
	}

	@SuppressWarnings("unchecked")
	private void generateExcelRouteItinerary(List<Customer> customers) {
		try {
			if (customers == null || customers.isEmpty())
				return;
			routeItineraryTable.build();
			routeItineraryTable.items(customers);
			service.saveAsExcel(routeItineraryTable);
		} catch (Exception e) {
			e.printStackTrace();
			dialog.show(e).addParent(stage).start();
		}
	}
}
