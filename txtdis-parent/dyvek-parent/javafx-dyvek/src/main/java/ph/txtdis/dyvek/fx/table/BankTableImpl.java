package ph.txtdis.dyvek.fx.table;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import ph.txtdis.dyvek.fx.dialog.BankDialog;
import ph.txtdis.dyvek.model.Customer;
import ph.txtdis.fx.table.AbstractNameListTable;

@Scope("prototype")
@Component("bankTable")
public class BankTableImpl //
	extends AbstractNameListTable<Customer, BankDialog> //
	implements BankTable {
}
