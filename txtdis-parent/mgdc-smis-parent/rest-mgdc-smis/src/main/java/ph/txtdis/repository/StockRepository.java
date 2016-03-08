package ph.txtdis.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import ph.txtdis.domain.Stock;

@Repository("stockRepository")
public interface StockRepository extends CrudRepository<Stock, Long> {
}
