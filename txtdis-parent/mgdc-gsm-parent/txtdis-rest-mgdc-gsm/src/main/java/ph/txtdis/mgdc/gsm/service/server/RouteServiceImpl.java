package ph.txtdis.mgdc.gsm.service.server;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ph.txtdis.dto.Route;
import ph.txtdis.mgdc.domain.RouteEntity;
import ph.txtdis.mgdc.service.server.AbstractRouteService;
import ph.txtdis.service.RestClientService;

import java.util.List;

import static java.util.stream.Collectors.toList;
import static java.util.stream.StreamSupport.stream;

@Service("routeService")
public class RouteServiceImpl
	extends AbstractRouteService //
	implements GsmRouteService {

	private static final String ROUTE = "route";

	@Autowired
	private RestClientService<Route> restClientService;

	@Override
	public void importAll() throws Exception {
		List<Route> l = restClientService.module(ROUTE).getList();
		repository.save(toEntities(l));
	}

	@Override
	public List<Route> list() {
		Iterable<RouteEntity> i = repository.findAll();
		return stream(i.spliterator(), false) //
			.map(e -> toModel(e)) //
			.collect(toList());
	}

	@Override
	@Transactional
	public Route save(Route c) {
		try {
			c = super.save(c);
			return saveToEdms(c);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public Route saveToEdms(Route c) throws Exception {
		return restClientService.module(ROUTE).save(c);
	}
}