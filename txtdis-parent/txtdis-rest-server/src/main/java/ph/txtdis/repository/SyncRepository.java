package ph.txtdis.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import ph.txtdis.domain.SyncEntity;
import ph.txtdis.type.SyncType;

@Repository("syncRepository")
public interface SyncRepository extends CrudRepository<SyncEntity, SyncType> {

	SyncEntity findByType(@Param("type") SyncType t);
}
