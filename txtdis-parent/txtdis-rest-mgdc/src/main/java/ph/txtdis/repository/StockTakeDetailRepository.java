package ph.txtdis.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import ph.txtdis.domain.StockTakeDetailEntity;

@Repository("stockTakeDetailRepository")
public interface StockTakeDetailRepository extends CrudRepository<StockTakeDetailEntity, Long> {
}
