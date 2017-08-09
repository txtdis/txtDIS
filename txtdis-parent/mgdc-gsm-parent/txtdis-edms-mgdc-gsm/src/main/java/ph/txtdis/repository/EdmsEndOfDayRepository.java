package ph.txtdis.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import ph.txtdis.domain.EdmsEndOfDay;

@Repository("edmsEndOfDayRepository")
public interface EdmsEndOfDayRepository
	extends CrudRepository<EdmsEndOfDay, Integer> {

	EdmsEndOfDay findFirstByOrderByEdmsDateDesc();
}