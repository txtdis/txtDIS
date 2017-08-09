package ph.txtdis.mgdc.gsm.service.server;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ph.txtdis.dto.Routing;
import ph.txtdis.mgdc.gsm.domain.CustomerEntity;
import ph.txtdis.mgdc.gsm.domain.RoutingEntity;
import ph.txtdis.mgdc.gsm.repository.RoutingRepository;
import ph.txtdis.mgdc.service.server.RouteService;

import java.util.List;

import static java.util.stream.Collectors.toList;

@Service("routingService")
public class RoutingServiceImpl
	implements RoutingService {

	@Autowired
	private RoutingRepository repository;

	@Autowired
	private RouteService routeService;

	@Override
	public List<RoutingEntity> toEntities(List<Routing> l, CustomerEntity c) {
		return l == null || c == null ? null : l.stream().map(r -> toEntity(c, r)).collect(toList());
	}

	private RoutingEntity toEntity(CustomerEntity c, Routing r) {
		RoutingEntity e = findEntity(r);
		return e != null ? e : newEntity(c, r);
	}

	private RoutingEntity findEntity(Routing t) {
		Long id = t.getId();
		return id == null ? null : repository.findOne(id);
	}

	private RoutingEntity newEntity(CustomerEntity c, Routing r) {
		RoutingEntity e = new RoutingEntity();
		e.setCustomer(c);
		e.setRoute(routeService.toEntity(r.getRoute()));
		e.setStartDate(r.getStartDate());
		return e;
	}

	@Override
	public List<Routing> toRoutings(List<RoutingEntity> l) {
		return l == null ? null : l.stream().map(e -> toRouting(e)).collect(toList());
	}

	private Routing toRouting(RoutingEntity e) {
		Routing r = new Routing();
		r.setId(e.getId());
		r.setRoute(routeService.toModel(e.getRoute()));
		r.setStartDate(e.getStartDate());
		r.setCreatedBy(e.getCreatedBy());
		r.setCreatedOn(e.getCreatedOn());
		return r;
	}
}