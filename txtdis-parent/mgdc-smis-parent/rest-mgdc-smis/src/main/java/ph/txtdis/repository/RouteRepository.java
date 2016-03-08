package ph.txtdis.repository;

import org.springframework.stereotype.Repository;

import ph.txtdis.domain.Route;

@Repository("routeRepository")
public interface RouteRepository extends NameListRepository<Route> {
}
