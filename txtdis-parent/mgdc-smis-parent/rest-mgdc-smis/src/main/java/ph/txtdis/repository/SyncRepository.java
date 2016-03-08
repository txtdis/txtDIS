package ph.txtdis.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import ph.txtdis.domain.Sync;
import ph.txtdis.type.SyncType;

@Repository("syncRepository")
public interface SyncRepository extends CrudRepository<Sync, SyncType> {

	Sync findByType(@Param("type") SyncType t);
}
