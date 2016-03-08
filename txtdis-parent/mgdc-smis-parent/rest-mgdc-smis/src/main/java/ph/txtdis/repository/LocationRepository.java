package ph.txtdis.repository;

import java.util.List;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import ph.txtdis.domain.Location;
import ph.txtdis.type.LocationType;

@Repository("locationRepository")
public interface LocationRepository extends CrudRepository<Location, Long> {

	List<Location> findByTypeOrderByNameAsc(@Param("type") LocationType type);
}
