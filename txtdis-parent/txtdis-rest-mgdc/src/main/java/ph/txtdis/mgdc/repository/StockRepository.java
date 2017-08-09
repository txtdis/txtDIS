package ph.txtdis.mgdc.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import ph.txtdis.mgdc.domain.StockEntity;

@Repository("stockRepository")
public interface StockRepository
	extends CrudRepository<StockEntity, Long> {
}
