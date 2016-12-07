package ph.txtdis.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import ph.txtdis.domain.RoutingEntity;

@Repository("routingRepository")
public interface RoutingRepository extends CrudRepository<RoutingEntity, Long> {
}
