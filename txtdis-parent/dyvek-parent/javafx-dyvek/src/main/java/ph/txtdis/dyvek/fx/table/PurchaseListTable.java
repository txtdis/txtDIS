package ph.txtdis.dyvek.fx.table;

import ph.txtdis.dyvek.model.Billable;
import ph.txtdis.fx.table.AppTable;

public interface PurchaseListTable //
	extends AppTable<Billable> {

	PurchaseListTable noStatusColumn();
}
