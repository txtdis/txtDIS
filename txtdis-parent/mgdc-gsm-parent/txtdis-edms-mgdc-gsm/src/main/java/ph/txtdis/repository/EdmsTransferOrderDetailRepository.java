package ph.txtdis.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import ph.txtdis.domain.EdmsTransferOrderDetail;

@Repository("edmsTransferOrderDetailRepository")
public interface EdmsTransferOrderDetailRepository
	extends CrudRepository<EdmsTransferOrderDetail, Long> {
}
