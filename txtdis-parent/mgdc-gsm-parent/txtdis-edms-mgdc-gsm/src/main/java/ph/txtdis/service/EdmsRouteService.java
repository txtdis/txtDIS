package ph.txtdis.service;

import ph.txtdis.dto.Route;

public interface EdmsRouteService
	extends SavedNameListService<Route> {

	Route toNameOnlyRouteFromSeller(String sellerCode);

	Route toNameOnlyRouteFromTruck(String truckCode);
}