package ph.txtdis.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import ph.txtdis.domain.EdmsTransferOrder;

@Repository("edmsTransferOrderRepository")
public interface EdmsTransferOrderRepository extends CrudRepository<EdmsTransferOrder, Long> {
}
