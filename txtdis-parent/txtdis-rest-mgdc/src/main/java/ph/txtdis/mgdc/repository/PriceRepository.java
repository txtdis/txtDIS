package ph.txtdis.mgdc.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import ph.txtdis.mgdc.domain.PriceEntity;

@Repository("priceRepository")
public interface PriceRepository
	extends CrudRepository<PriceEntity, Long> {
}
