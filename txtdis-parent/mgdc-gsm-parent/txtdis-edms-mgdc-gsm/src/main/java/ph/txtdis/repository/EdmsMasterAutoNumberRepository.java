package ph.txtdis.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ph.txtdis.domain.EdmsMasterAutoNumber;

@Repository("edmsMasterAutoNumberRepository")
public interface EdmsMasterAutoNumberRepository
	extends CrudRepository<EdmsMasterAutoNumber, Integer> {

	EdmsMasterAutoNumber findByName(@Param("name") String n);
}
