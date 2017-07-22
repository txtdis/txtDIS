package ph.txtdis.dyvek.app;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import ph.txtdis.app.AbstractTableApp;
import ph.txtdis.dyvek.fx.table.TruckingClientTable;
import ph.txtdis.dyvek.model.Customer;
import ph.txtdis.dyvek.service.TruckingClientService;

@Scope("prototype")
@Component("truckingClientApp")
public class TruckingClientAppImpl //
		extends AbstractTableApp<TruckingClientTable, TruckingClientService, Customer> //
		implements TruckingClientApp {
}
