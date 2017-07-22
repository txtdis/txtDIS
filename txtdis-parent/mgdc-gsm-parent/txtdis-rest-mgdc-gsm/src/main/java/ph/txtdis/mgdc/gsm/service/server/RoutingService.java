package ph.txtdis.mgdc.gsm.service.server;

import java.util.List;

import ph.txtdis.dto.Routing;
import ph.txtdis.mgdc.gsm.domain.CustomerEntity;
import ph.txtdis.mgdc.gsm.domain.RoutingEntity;

public interface RoutingService {

	List<RoutingEntity> toEntities(List<Routing> l, CustomerEntity c);

	List<Routing> toRoutings(List<RoutingEntity> l);
}