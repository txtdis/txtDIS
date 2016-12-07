package ph.txtdis.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import ph.txtdis.domain.CustomerVolumePromoEntity;

@Repository("customerVolumePromoRepository")
public interface CustomerVolumePromoRepository extends CrudRepository<CustomerVolumePromoEntity, Long> {
}
