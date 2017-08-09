package ph.txtdis.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ph.txtdis.domain.EdmsAutoNumber;

@Repository("edmsAutoNumberRepository")
public interface EdmsAutoNumberRepository
	extends CrudRepository<EdmsAutoNumber, Integer> {

	EdmsAutoNumber findByUpdateNo(@Param("updateNo") String n);
}
