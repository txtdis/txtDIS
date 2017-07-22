package ph.txtdis.dyvek.fx.table;

import javafx.beans.value.ObservableBooleanValue;
import ph.txtdis.dyvek.model.BillableDetail;
import ph.txtdis.fx.table.AppTable;

public interface BillingTable //
		extends AppTable<BillableDetail> {

	BillingTable showSelectionColumnIf(ObservableBooleanValue b);
}
