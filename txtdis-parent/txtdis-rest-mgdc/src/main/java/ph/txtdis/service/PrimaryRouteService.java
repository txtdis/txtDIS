package ph.txtdis.service;

import ph.txtdis.domain.RouteEntity;
import ph.txtdis.dto.Route;

public interface PrimaryRouteService extends RouteService {

	Route toDTO(RouteEntity route);

	RouteEntity toEntity(Route route);
}