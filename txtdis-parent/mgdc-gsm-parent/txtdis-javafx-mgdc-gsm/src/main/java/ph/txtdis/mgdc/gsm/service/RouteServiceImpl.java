package ph.txtdis.mgdc.gsm.service;

import org.springframework.stereotype.Service;
import ph.txtdis.dto.Route;
import ph.txtdis.service.RestClientService;

import java.util.List;

@Service("routeService")
public class RouteServiceImpl //
	extends AbstractRouteService //
	implements ItineraryRouteService {

	@Override
	public List<Route> listExTruckRoutes() {
		try {
			return getRouteService().getList("/extruck");
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	private RestClientService<Route> getRouteService() {
		return getRestClientService().module(getModuleName());
	}

	@Override
	public List<Route> listPreSellRoutes() {
		try {
			return getRouteService().getList("/presell");
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
}
