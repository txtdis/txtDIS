package ph.txtdis.dyvek.fx.table;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import ph.txtdis.dyvek.fx.dialog.TruckingClientDialog;
import ph.txtdis.dyvek.model.Customer;
import ph.txtdis.fx.table.AbstractNameListTable;

@Scope("prototype")
@Component("truckingClientTable")
public class TruckingClientTableImpl //
		extends AbstractNameListTable<Customer, TruckingClientDialog> //
		implements TruckingClientTable {
}
