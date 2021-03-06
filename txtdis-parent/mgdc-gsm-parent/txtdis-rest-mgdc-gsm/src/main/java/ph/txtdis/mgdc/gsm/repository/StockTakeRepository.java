package ph.txtdis.mgdc.gsm.repository;

import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ph.txtdis.mgdc.gsm.domain.StockTakeEntity;
import ph.txtdis.repository.SpunRepository;

import java.time.LocalDate;

@Repository("stockTakeRepository")
public interface StockTakeRepository //
	extends SpunRepository<StockTakeEntity, Long> {

	StockTakeEntity findByStockTakeDate( //
	                                     @Param("countDate") LocalDate d);

	StockTakeEntity findFirstByStockTakeDateLessThanEqualOrderByStockTakeDateDesc( //
	                                                                               @Param("countDate") LocalDate d);

	StockTakeEntity findFirstByWarehouseNameAndStockTakeDate( //
	                                                          @Param("warehouse") String s, //
	                                                          @Param("countDate") LocalDate d);
}
