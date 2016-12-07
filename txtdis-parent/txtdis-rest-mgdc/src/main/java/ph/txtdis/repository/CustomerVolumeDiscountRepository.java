package ph.txtdis.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import ph.txtdis.domain.CustomerVolumeDiscountEntity;

@Repository("customerVolumeDiscountRepository")
public interface CustomerVolumeDiscountRepository extends CrudRepository<CustomerVolumeDiscountEntity, Long> {
}
