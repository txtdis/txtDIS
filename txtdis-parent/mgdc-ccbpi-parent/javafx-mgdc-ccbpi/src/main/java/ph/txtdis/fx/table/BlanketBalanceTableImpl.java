package ph.txtdis.fx.table;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import ph.txtdis.service.BlanketBalanceService;

@Scope("prototype")
@Component("blanketBalanceTable")
public class BlanketBalanceTableImpl extends AbstractVarianceTable<BlanketBalanceService>
		implements BlanketBalanceTable {

	@Override
	protected void addProperties() {
		menu.setMenu(service, this);
	}
}
