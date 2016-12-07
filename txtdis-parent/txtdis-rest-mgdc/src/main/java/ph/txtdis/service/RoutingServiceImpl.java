package ph.txtdis.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ph.txtdis.domain.RoutingEntity;
import ph.txtdis.dto.Routing;
import ph.txtdis.repository.RoutingRepository;

@Service("routingService")
public class RoutingServiceImpl implements RoutingService {

	@Autowired
	private RoutingRepository repository;

	@Autowired
	private PrimaryRouteService routeService;

	@Override
	public List<Routing> toRoutings(List<RoutingEntity> l) {
		return l == null ? null : l.stream().map(e -> toRouting(e)).collect(Collectors.toList());
	}

	private Routing toRouting(RoutingEntity e) {
		Routing r = new Routing();
		r.setId(e.getId());
		r.setRoute(routeService.toDTO(e.getRoute()));
		r.setStartDate(e.getStartDate());
		r.setCreatedBy(e.getCreatedBy());
		r.setCreatedOn(e.getCreatedOn());
		return r;
	}

	@Override
	public List<RoutingEntity> toEntities(List<Routing> l) {
		return l == null ? null : l.stream().map(r -> toEntity(r)).collect(Collectors.toList());
	}

	private RoutingEntity toEntity(Routing t) {
		RoutingEntity r = findSavedEntity(t);
		return r != null ? r : createEntity(t);
	}

	private RoutingEntity findSavedEntity(Routing t) {
		Long id = t.getId();
		return id == null ? null : repository.findOne(t.getId());
	}

	private RoutingEntity createEntity(Routing t) {
		RoutingEntity r = new RoutingEntity();
		r.setRoute(routeService.toEntity(t.getRoute()));
		r.setStartDate(t.getStartDate());
		return r;
	}
}