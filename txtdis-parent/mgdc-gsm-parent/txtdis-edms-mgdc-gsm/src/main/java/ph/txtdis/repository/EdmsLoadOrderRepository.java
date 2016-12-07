package ph.txtdis.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import ph.txtdis.domain.EdmsLoadOrder;

@Repository("edmsLoadOrderRepository")
public interface EdmsLoadOrderRepository extends CrudRepository<EdmsLoadOrder, Long> {

	EdmsLoadOrder findByReferenceNo(@Param("referenceNo") String referenceNo);
}