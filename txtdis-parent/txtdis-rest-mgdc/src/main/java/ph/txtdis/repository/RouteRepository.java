package ph.txtdis.repository;

import java.util.List;

import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import ph.txtdis.domain.RouteEntity;
import ph.txtdis.type.DeliveryType;

@Repository("routeRepository")
public interface RouteRepository extends NameListRepository<RouteEntity> {

	List<RouteEntity> findByNameStartingWith(@Param("name") String n);

	List<RouteEntity> findByType(@Param("delivered") DeliveryType d);
}
