package ph.txtdis.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ph.txtdis.dto.Route;
import ph.txtdis.exception.FailedAuthenticationException;
import ph.txtdis.exception.InvalidException;
import ph.txtdis.exception.NoServerConnectionException;
import ph.txtdis.exception.RestException;
import ph.txtdis.exception.StoppedServerException;

@Service("routeService")
public class RouteServiceImpl extends AbstractRouteService implements ImportedRouteService {

	@Autowired
	private ReadOnlyService<Route> readOnlyService;

	@Override
	public void importAll() throws NoServerConnectionException, StoppedServerException, FailedAuthenticationException,
			RestException, InvalidException {
		List<Route> l = readOnlyService.module("route").getList();
		repository.save(toEntities(l));
	}
}