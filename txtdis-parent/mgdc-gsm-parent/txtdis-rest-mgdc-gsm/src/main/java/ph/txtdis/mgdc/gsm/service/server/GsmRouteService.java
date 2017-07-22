package ph.txtdis.mgdc.gsm.service.server;

import java.util.List;

import ph.txtdis.dto.Route;
import ph.txtdis.mgdc.service.server.RouteService;

public interface GsmRouteService
		extends RouteService, Imported {

	List<Route> list();

	Route saveToEdms(Route r) throws Exception;
}
