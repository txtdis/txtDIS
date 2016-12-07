package ph.txtdis.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import ph.txtdis.domain.EdmsDeliveryDetail;

@Repository("edmsDeliveryDetailRepository")
public interface EdmsDeliveryDetailRepository extends CrudRepository<EdmsDeliveryDetail, Short> {
}
