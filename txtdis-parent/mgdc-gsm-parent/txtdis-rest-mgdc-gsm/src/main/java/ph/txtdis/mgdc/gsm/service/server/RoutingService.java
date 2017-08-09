package ph.txtdis.mgdc.gsm.service.server;

import ph.txtdis.dto.Routing;
import ph.txtdis.mgdc.gsm.domain.CustomerEntity;
import ph.txtdis.mgdc.gsm.domain.RoutingEntity;

import java.util.List;

public interface RoutingService {

	List<RoutingEntity> toEntities(List<Routing> l, CustomerEntity c);

	List<Routing> toRoutings(List<RoutingEntity> l);
}