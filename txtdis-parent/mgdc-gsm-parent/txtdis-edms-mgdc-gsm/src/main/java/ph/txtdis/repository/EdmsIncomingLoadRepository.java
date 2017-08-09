package ph.txtdis.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ph.txtdis.domain.EdmsIncomingLoad;

@Repository("edmsIncomingLoadRepository")
public interface EdmsIncomingLoadRepository
	extends CrudRepository<EdmsIncomingLoad, Short> {

	EdmsIncomingLoad findByReferenceNo(@Param("referenceNo") String r);
}
