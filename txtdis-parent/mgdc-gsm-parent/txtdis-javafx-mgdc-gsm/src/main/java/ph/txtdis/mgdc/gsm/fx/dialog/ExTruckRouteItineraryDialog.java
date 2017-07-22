package ph.txtdis.mgdc.gsm.fx.dialog;

import java.util.List;

import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import ph.txtdis.dto.Route;

@Lazy
//@Scope("prototype")
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
