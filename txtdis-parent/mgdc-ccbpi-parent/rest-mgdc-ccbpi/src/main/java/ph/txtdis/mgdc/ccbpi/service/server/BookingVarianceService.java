package ph.txtdis.mgdc.ccbpi.service.server;

import java.time.LocalDate;
import java.util.List;

import ph.txtdis.dto.SalesItemVariance;

public interface BookingVarianceService {

	List<SalesItemVariance> list(String route, LocalDate start, LocalDate end);

	List<SalesItemVariance> listDDL(String item, String route, LocalDate start, LocalDate end);

	List<SalesItemVariance> listOCS(String item, String route, LocalDate start, LocalDate end);

	List<SalesItemVariance> listRR(String item, String route, LocalDate start, LocalDate end);
}
