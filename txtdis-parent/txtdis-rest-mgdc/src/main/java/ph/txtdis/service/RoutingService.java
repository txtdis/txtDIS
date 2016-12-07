package ph.txtdis.service;

import java.util.List;

import ph.txtdis.domain.RoutingEntity;
import ph.txtdis.dto.Routing;

public interface RoutingService {

	List<Routing> toRoutings(List<RoutingEntity> l);

	List<RoutingEntity> toEntities(List<Routing> routeHistory);
}