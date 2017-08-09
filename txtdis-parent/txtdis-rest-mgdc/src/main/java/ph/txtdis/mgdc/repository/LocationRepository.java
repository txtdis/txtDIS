package ph.txtdis.mgdc.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ph.txtdis.mgdc.domain.LocationEntity;
import ph.txtdis.type.LocationType;

import java.util.List;

@Repository("locationRepository")
public interface LocationRepository
	extends CrudRepository<LocationEntity, Long> {

	List<LocationEntity> findByTypeOrderByNameAsc(@Param("type") LocationType t);

	LocationEntity findByNameIgnoreCase(@Param("name") String n);

	LocationEntity findByName(@Param("name") String n);

	LocationEntity findByNameAndType(@Param("name") String n, @Param("type") LocationType t);
}
