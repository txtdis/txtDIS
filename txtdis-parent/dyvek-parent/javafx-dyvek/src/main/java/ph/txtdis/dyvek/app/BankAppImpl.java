package ph.txtdis.dyvek.app;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import ph.txtdis.app.AbstractTableApp;
import ph.txtdis.dyvek.fx.table.BankTable;
import ph.txtdis.dyvek.model.Customer;
import ph.txtdis.dyvek.service.BankService;

@Scope("prototype")
@Component("bankApp")
public class BankAppImpl
	extends AbstractTableApp<BankTable, BankService, Customer>
	implements BankApp {
}
