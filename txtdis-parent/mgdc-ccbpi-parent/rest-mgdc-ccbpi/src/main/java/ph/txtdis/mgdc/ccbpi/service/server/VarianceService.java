package ph.txtdis.mgdc.ccbpi.service.server;

import java.time.LocalDate;
import java.util.List;

import ph.txtdis.dto.SalesItemVariance;

public interface VarianceService {

	List<SalesItemVariance> list(LocalDate start, LocalDate end);
}
