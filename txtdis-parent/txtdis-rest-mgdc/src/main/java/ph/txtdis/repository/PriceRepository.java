package ph.txtdis.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import ph.txtdis.domain.PriceEntity;

@Repository("priceRepository")
public interface PriceRepository extends CrudRepository<PriceEntity, Long> {
}
