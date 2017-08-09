package ph.txtdis.mgdc.service.server;

import ph.txtdis.dto.StockTakeVariance;

import java.time.LocalDate;
import java.util.List;

public interface StockTakeVarianceService {

	List<StockTakeVariance> list(LocalDate date);
}