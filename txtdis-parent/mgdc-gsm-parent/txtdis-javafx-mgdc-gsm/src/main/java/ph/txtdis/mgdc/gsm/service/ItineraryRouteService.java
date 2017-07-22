package ph.txtdis.mgdc.gsm.service;

import java.util.List;

import ph.txtdis.dto.Route;
import ph.txtdis.mgdc.service.RouteService;

public interface ItineraryRouteService //
		extends RouteService {

	List<Route> listExTruckRoutes();

	List<Route> listPreSellRoutes();
}
