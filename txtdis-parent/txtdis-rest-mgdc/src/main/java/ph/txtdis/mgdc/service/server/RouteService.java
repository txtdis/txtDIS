package ph.txtdis.mgdc.service.server;

import java.util.List;

import ph.txtdis.dto.Route;
import ph.txtdis.mgdc.domain.RouteEntity;
import ph.txtdis.service.SavedNameListService;

public interface RouteService //
		extends ConvertibleService<RouteEntity, Route>, SavedNameListService<Route> {

	Route findByPrimaryKey(Long id);

	List<Route> listExTruckRoutes();

	List<Route> listPreSellRoutes();
}