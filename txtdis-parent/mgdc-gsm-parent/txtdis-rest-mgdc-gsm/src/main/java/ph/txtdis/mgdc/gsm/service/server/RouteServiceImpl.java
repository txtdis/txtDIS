package ph.txtdis.mgdc.gsm.service.server;

import static java.util.stream.Collectors.toList;
import static java.util.stream.StreamSupport.stream;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ph.txtdis.dto.Route;
import ph.txtdis.mgdc.domain.RouteEntity;
import ph.txtdis.mgdc.service.server.AbstractRouteService;
import ph.txtdis.service.ReadOnlyService;
import ph.txtdis.service.SavingService;

@Service("routeService")
public class RouteServiceImpl
		extends AbstractRouteService //
		implements GsmRouteService {

	private static final String ROUTE = "route";

	@Autowired
	private SavingService<Route> savingService;

	@Autowired
	private ReadOnlyService<Route> readOnlyService;

	@Override
	public void importAll() throws Exception {
		List<Route> l = readOnlyService.module(ROUTE).getList();
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
		return savingService.module(ROUTE).save(c);
	}
}