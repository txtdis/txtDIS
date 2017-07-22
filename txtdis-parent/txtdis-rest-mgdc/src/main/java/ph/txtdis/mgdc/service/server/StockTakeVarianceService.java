package ph.txtdis.mgdc.service.server;

import java.time.LocalDate;
import java.util.List;

import ph.txtdis.dto.StockTakeVariance;

public interface StockTakeVarianceService {

	List<StockTakeVariance> list(LocalDate date);
}