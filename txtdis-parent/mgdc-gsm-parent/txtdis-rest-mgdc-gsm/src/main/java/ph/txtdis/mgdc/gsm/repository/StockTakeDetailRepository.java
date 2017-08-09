package ph.txtdis.mgdc.gsm.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import ph.txtdis.mgdc.gsm.domain.StockTakeDetailEntity;

@Repository("stockTakeDetailRepository")
public interface StockTakeDetailRepository
	extends CrudRepository<StockTakeDetailEntity, Long> {
}
