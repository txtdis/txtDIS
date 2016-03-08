package ph.txtdis.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import ph.txtdis.domain.ItemList;

@Repository("soldDetailRepository")
public interface SoldDetailRepository extends CrudRepository<ItemList, Long> {
}
