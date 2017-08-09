package ph.txtdis.dyvek.app;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import ph.txtdis.app.AbstractTableApp;
import ph.txtdis.dyvek.fx.table.CashAdvanceTable;
import ph.txtdis.dyvek.model.CashAdvance;
import ph.txtdis.dyvek.service.CashAdvanceService;

@Scope("prototype")
@Component("cashAdvanceApp")
public class CashAdvanceAppImpl
	extends AbstractTableApp<CashAdvanceTable, CashAdvanceService, CashAdvance>
	implements CashAdvanceApp {
}
