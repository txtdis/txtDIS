package ph.txtdis.service;

import ph.txtdis.dto.Route;

public interface RouteService extends NameListCreateService<Route> {

	Route toNameOnlyDTOFromSeller(String sellerCode);

	Route toNameOnlyDTOFromTruck(String truckCode);
}