package ph.txtdis.mgdc.gsm.service;

import ph.txtdis.dto.Route;
import ph.txtdis.mgdc.service.RouteService;

import java.util.List;

public interface ItineraryRouteService //
	extends RouteService {

	List<Route> listExTruckRoutes();

	List<Route> listPreSellRoutes();
}
