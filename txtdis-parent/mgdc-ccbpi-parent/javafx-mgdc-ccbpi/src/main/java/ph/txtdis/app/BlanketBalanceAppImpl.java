package ph.txtdis.app;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import ph.txtdis.dto.SalesItemVariance;
import ph.txtdis.fx.table.BlanketBalanceTable;
import ph.txtdis.service.BlanketBalanceService;

@Scope("prototype")
@Component("blanketBalanceApp")
public class BlanketBalanceAppImpl
		extends AbstractTotaledReportApp<BlanketBalanceTable, BlanketBalanceService, SalesItemVariance>
		implements BlanketBalanceApp {

	@Override
	protected int noOfTotalDisplays() {
		return 4;
	}
}
