package ph.txtdis.mgdc.gsm.fx.dialog;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import ph.txtdis.dto.Route;

import java.util.List;

@Scope("prototype")
@Component("exTruckRouteItineraryDialog")
public class ExTruckRouteItineraryDialog //
	extends AbstractGsmRouteItineraryDialog {

	@Override
	protected List<Route> getRoutes() {
		try {
			return routeService.listExTruckRoutes();
		} catch (Exception e) {
			return null;
		}
	}
}
