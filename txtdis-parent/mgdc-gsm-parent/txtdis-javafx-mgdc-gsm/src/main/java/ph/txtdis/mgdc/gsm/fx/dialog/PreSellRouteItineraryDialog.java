package ph.txtdis.mgdc.gsm.fx.dialog;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import ph.txtdis.dto.Route;

import java.util.List;

@Scope("prototype")
@Component("preSellRouteItineraryDialog")
public class PreSellRouteItineraryDialog
	extends AbstractGsmRouteItineraryDialog {

	@Override
	protected List<Route> getRoutes() {
		try {
			return routeService.listPreSellRoutes();
		} catch (Exception e) {
			return null;
		}
	}
}
