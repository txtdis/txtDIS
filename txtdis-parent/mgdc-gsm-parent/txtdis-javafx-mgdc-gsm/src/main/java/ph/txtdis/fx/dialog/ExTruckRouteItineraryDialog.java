package ph.txtdis.fx.dialog;

import java.util.List;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import ph.txtdis.dto.Route;

@Scope("prototype")
@Component("exTruckRouteItineraryDialog")
public class ExTruckRouteItineraryDialog extends AbstractRouteItineraryDialog {

	@Override
	protected List<Route> getRoutes() {
		try {
			return routeService.listExTruckRoutes();
		} catch (Exception e) {
			return null;
		}
	}
}
