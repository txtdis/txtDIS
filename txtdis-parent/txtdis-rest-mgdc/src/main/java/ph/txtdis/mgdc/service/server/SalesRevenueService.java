package ph.txtdis.mgdc.service.server;

import ph.txtdis.dto.SalesRevenue;
import ph.txtdis.exception.DateBeforeGoLiveException;
import ph.txtdis.exception.EndDateBeforeStartException;

import java.time.LocalDate;
import java.util.List;

public interface SalesRevenueService {

	List<SalesRevenue> list(LocalDate start, LocalDate end)
		throws DateBeforeGoLiveException, EndDateBeforeStartException;
}