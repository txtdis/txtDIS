package ph.txtdis.dyvek.app;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import ph.txtdis.dyvek.fx.table.CashAdvanceListTable;
import ph.txtdis.dyvek.model.CashAdvance;
import ph.txtdis.dyvek.service.CashAdvanceService;

@Scope("prototype")
@Component("cashAdvanceListApp")
public class CashAdvanceListAppImpl
	extends AbstractListApp<CashAdvanceListTable, CashAdvanceService, CashAdvance>
	implements CashAdvanceListApp {
}
