package ph.txtdis.mgdc.ccbpi.fx.dialog;

import ph.txtdis.app.App;
import ph.txtdis.mgdc.ccbpi.dto.Customer;

public interface CustomerDialog
	extends App {

	Customer getCustomer();

	CustomerDialog outletName(String name);

	CustomerDialog vendorId(Long id);
}