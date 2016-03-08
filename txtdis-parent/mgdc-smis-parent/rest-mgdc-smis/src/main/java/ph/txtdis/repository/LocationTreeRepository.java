package ph.txtdis.repository;

import java.util.List;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import ph.txtdis.domain.Location;
import ph.txtdis.domain.LocationTree;
import ph.txtdis.type.LocationType;

@Repository("locationTreeRepository")
public interface LocationTreeRepository extends CrudRepository<LocationTree, Long> {

	List<LocationTree> findByLocationTypeAndParentOrderByLocationNameAsc(@Param("type") LocationType type,
			@Param("parent") Location parent);
}
