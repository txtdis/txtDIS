package ph.txtdis.app;

import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import ph.txtdis.dto.Customer;
import ph.txtdis.fx.table.CustomerTable;
import ph.txtdis.service.CustomerService;

@Lazy
@Component("customerApp")
public class CustomerApp extends AbstractTableApp<CustomerTable, CustomerService, Customer> {

	public Customer getSelection() {
		return table.getItem();
	}
}
