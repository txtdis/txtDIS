package ph.txtdis.service;

import java.util.List;

import org.springframework.stereotype.Service;

import ph.txtdis.dto.Route;

@Service("routeService")
public class RouteServiceImpl extends AbstractRouteService implements ItineraryRouteService {

	@Override
	public List<Route> listExTruckRoutes() {
		try {
			return readOnlyService.module(getModule()).getList("/extruck");
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	@Override
	public List<Route> listPreSellRoutes() {
		try {
			return readOnlyService.module(getModule()).getList("/presell");
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
}
