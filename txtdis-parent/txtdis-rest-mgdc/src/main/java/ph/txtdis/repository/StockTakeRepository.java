package ph.txtdis.repository;

import java.time.LocalDate;

import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import ph.txtdis.domain.StockTakeEntity;

@Repository("stockTakeRepository")
public interface StockTakeRepository extends SpunRepository<StockTakeEntity, Long> {

	StockTakeEntity findByStockTakeDate(@Param("countDate") LocalDate d);

	StockTakeEntity findFirstByStockTakeDateLessThanEqualOrderByStockTakeDateDesc(@Param("countDate") LocalDate d);

	StockTakeEntity findFirstByWarehouseNameAndStockTakeDate(@Param("warehouse") String s,
			@Param("countDate") LocalDate d);
}
