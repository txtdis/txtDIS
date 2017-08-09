package ph.txtdis.mgdc.app;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import ph.txtdis.app.AbstractTotaledReportApp;
import ph.txtdis.dto.SalesRevenue;
import ph.txtdis.mgdc.fx.table.SalesRevenueTable;
import ph.txtdis.mgdc.service.SalesRevenueService;

@Scope("prototype")
@Component("salesRevenueApp")
public class SalesRevenueApp
	extends AbstractTotaledReportApp<SalesRevenueTable, SalesRevenueService, SalesRevenue> {

	@Override
	protected int noOfTotalDisplays() {
		return 2;
	}
}
