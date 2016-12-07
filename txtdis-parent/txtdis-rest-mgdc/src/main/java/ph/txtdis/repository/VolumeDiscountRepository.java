package ph.txtdis.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import ph.txtdis.domain.VolumeDiscountEntity;

@Repository("volumeDiscountRepository")
public interface VolumeDiscountRepository extends CrudRepository<VolumeDiscountEntity, Long> {
}
