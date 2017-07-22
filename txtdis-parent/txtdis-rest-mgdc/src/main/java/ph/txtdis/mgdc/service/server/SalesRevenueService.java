package ph.txtdis.mgdc.service.server;

import java.time.LocalDate;
import java.util.List;

import ph.txtdis.dto.SalesRevenue;
import ph.txtdis.exception.DateBeforeGoLiveException;
import ph.txtdis.exception.EndDateBeforeStartException;

public interface SalesRevenueService {

	List<SalesRevenue> list(LocalDate start, LocalDate end)
			throws DateBeforeGoLiveException, EndDateBeforeStartException;
}