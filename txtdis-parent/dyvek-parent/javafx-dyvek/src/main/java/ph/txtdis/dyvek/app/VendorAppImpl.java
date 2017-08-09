package ph.txtdis.dyvek.app;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import ph.txtdis.app.AbstractTableApp;
import ph.txtdis.dyvek.fx.table.VendorTable;
import ph.txtdis.dyvek.model.Customer;
import ph.txtdis.dyvek.service.VendorService;

@Scope("prototype")
@Component("vendorApp")
public class VendorAppImpl
	extends AbstractTableApp<VendorTable, VendorService, Customer>
	implements VendorApp {
}
