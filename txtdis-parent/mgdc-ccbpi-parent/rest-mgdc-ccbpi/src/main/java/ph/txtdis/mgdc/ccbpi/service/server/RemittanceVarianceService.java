package ph.txtdis.mgdc.ccbpi.service.server;

import java.time.LocalDate;
import java.util.List;

import ph.txtdis.dto.SalesItemVariance;

public interface RemittanceVarianceService {

	List<SalesItemVariance> list(String collector, LocalDate start, LocalDate end);

	List<SalesItemVariance> listDelivered(String route, LocalDate start, LocalDate end);

	List<SalesItemVariance> listLoaded(String route, LocalDate start, LocalDate end);

	List<SalesItemVariance> listUnpicked(String route, LocalDate start, LocalDate end);
}
