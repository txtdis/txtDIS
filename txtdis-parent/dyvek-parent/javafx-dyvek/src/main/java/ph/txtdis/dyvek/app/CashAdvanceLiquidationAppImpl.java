package ph.txtdis.dyvek.app;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import ph.txtdis.app.AbstractTableApp;
import ph.txtdis.dto.RemittanceDetail;
import ph.txtdis.dyvek.fx.table.CashAdvanceLiquidationTable;
import ph.txtdis.dyvek.service.CashAdvanceLiquidationService;

@Scope("prototype")
@Component("cashAdvanceLiquidationApp")
public class CashAdvanceLiquidationAppImpl
	extends AbstractTableApp<CashAdvanceLiquidationTable, CashAdvanceLiquidationService, RemittanceDetail>
	implements CashAdvanceLiquidationApp {

	@Override
	public void actOn(String... keys) {
		try {
			service.openByDoubleClickedTableCellKey(keys[0]);
		} catch (Exception e) {
			showErrorDialog(e);
		} finally {
			refresh();
		}
	}
}
