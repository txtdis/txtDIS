package ph.txtdis.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import ph.txtdis.domain.EdmsAgingReceivable;

@Repository("edmsAgingReceivableRepository")
public interface EdmsAgingReceivableRepository //
		extends CrudRepository<EdmsAgingReceivable, Integer> {
}
