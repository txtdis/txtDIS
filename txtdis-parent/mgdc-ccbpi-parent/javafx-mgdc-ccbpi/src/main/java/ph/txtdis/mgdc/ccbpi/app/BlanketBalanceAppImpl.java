package ph.txtdis.mgdc.ccbpi.app;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import ph.txtdis.app.AbstractTotaledReportApp;
import ph.txtdis.dto.SalesItemVariance;
import ph.txtdis.fx.control.AppButton;
import ph.txtdis.mgdc.ccbpi.fx.table.BlanketBalanceTable;
import ph.txtdis.mgdc.ccbpi.service.BlanketBalanceService;

import java.util.List;

import static java.util.Arrays.asList;

@Scope("prototype")
@Component("blanketBalanceApp")
public class BlanketBalanceAppImpl //
	extends AbstractTotaledReportApp<BlanketBalanceTable, BlanketBalanceService, SalesItemVariance> //
	implements BlanketBalanceApp {

	@Override
	protected List<AppButton> addButtons() {
		createButtons();
		setOnButtonClick();
		return asList(excelButton);
	}

	@Override
	protected int noOfTotalDisplays() {
		return 5;
	}
}
