package ph.txtdis.mgdc.repository;

import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ph.txtdis.mgdc.domain.RouteEntity;
import ph.txtdis.repository.NameListRepository;

import java.util.List;

@Repository("routeRepository")
public interface RouteRepository //
	extends NameListRepository<RouteEntity> {

	List<RouteEntity> findByNameStartingWith( //
	                                          @Param("name") String n);
}
