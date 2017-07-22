package ph.txtdis.mgdc.gsm.service.server;

import java.time.LocalDate;

import ph.txtdis.dto.StockTake;
import ph.txtdis.mgdc.gsm.domain.StockTakeEntity;

public interface StockTakeService {

	StockTake find(Long id);

	StockTake findByDate(LocalDate d) throws Exception;

	StockTake findByWarehouseAndDate(String s, LocalDate d);

	StockTakeEntity findEntityByCountDate(LocalDate d);

	StockTakeEntity findLatestEntityOnOrBeforeCutoff(LocalDate d);

	StockTake first();

	StockTake firstToSpin();

	StockTake last();

	StockTake lastToSpin();

	StockTake next(Long id);

	StockTake previous(Long id);

	StockTake save(StockTake s);
}