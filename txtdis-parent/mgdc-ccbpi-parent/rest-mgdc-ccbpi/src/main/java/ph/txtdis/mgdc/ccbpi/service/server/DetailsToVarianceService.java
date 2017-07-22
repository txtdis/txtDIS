package ph.txtdis.mgdc.ccbpi.service.server;

import java.time.LocalDate;
import java.util.List;

import ph.txtdis.dto.SalesItemVariance;

public interface DetailsToVarianceService {

	void addActualService(FilteredListService service);

	void addExpectedService(FilteredListService service);

	void addOtherService(FilteredListService service);

	void addReturnedService(FilteredListService service);

	List<SalesItemVariance> list(String filter, LocalDate start, LocalDate end);
}