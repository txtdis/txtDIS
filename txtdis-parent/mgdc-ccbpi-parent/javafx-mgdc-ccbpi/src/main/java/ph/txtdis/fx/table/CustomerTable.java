package ph.txtdis.fx.table;

import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import ph.txtdis.dto.Customer;
import ph.txtdis.fx.dialog.CustomerDialog;

@Lazy
@Component("customerTable")
public class CustomerTable extends NameListTable<Customer, CustomerDialog> {

	@Override
	protected void addColumns() {
		super.addColumns();
		getColumns().set(0, id.build("Code", "code"));
	}
}
