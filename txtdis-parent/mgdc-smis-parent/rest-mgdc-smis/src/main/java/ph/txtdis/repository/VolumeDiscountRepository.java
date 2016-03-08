package ph.txtdis.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import ph.txtdis.domain.VolumeDiscount;

@Repository("volumeDiscountRepository")
public interface VolumeDiscountRepository extends CrudRepository<VolumeDiscount, Long> {
}
