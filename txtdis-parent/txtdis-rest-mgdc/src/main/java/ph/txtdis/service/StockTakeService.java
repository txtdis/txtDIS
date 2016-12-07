package ph.txtdis.service;

import java.sql.Date;
import java.time.LocalDate;

import ph.txtdis.domain.StockTakeEntity;
import ph.txtdis.dto.StockTake;

public interface StockTakeService {

	StockTake find(Long id);

	StockTake findByDate(Date d);

	StockTake findByWarehouseAndDate(String s, Date d);

	StockTakeEntity findEntityByCountDate(LocalDate d);

	StockTakeEntity findLatestEntityOnOrBeforeCutoff(LocalDate cutoff);

	StockTake first();

	StockTake firstToSpin();

	StockTake last();

	StockTake lastToSpin();

	StockTake next(Long id);

	StockTake previous(Long id);

	StockTake save(StockTake a);
}