package ph.txtdis.mgdc.gsm.service.server;

import ph.txtdis.dto.Route;
import ph.txtdis.mgdc.service.server.RouteService;

import java.util.List;

public interface GsmRouteService
	extends RouteService,
	Imported {

	List<Route> list();

	Route saveToEdms(Route r) throws Exception;
}
