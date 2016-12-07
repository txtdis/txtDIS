package ph.txtdis.service;

import java.util.List;

import ph.txtdis.domain.RouteEntity;
import ph.txtdis.dto.Route;

public interface RouteService extends NameListCreateService<Route> {

	Route findById(Long id);

	List<Route> listExTruckRoutes();

	List<Route> listPreSellRoutes();

	List<RouteEntity> listDelivered();
}