package ph.txtdis.mgdc.repository;

import java.util.List;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import ph.txtdis.mgdc.domain.LocationEntity;
import ph.txtdis.mgdc.domain.LocationTreeEntity;
import ph.txtdis.type.LocationType;

@Repository("locationTreeRepository")
public interface LocationTreeRepository extends CrudRepository<LocationTreeEntity, Long> {

	List<LocationTreeEntity> findByLocationTypeAndParentNameOrderByLocationNameAsc(@Param("type") LocationType t,
			@Param("parent") String parent);

	LocationTreeEntity findByLocationTypeAndParentAndLocationNameOrderByLocationNameAsc(@Param("type") LocationType t,
			@Param("parent") LocationEntity p, @Param("location") String c);
}
