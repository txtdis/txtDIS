package ph.txtdis.service;

import java.util.List;

import ph.txtdis.dto.Route;

public interface ItineraryRouteService extends RouteService {

	List<Route> listExTruckRoutes();

	List<Route> listPreSellRoutes();
}
