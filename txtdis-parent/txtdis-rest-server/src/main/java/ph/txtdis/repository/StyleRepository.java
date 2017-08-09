package ph.txtdis.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import ph.txtdis.domain.StyleEntity;

@Repository("styleRepository")
public interface StyleRepository
	extends CrudRepository<StyleEntity, Long> {
}
