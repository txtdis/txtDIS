package ph.txtdis.dyvek.fx.table;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import ph.txtdis.dyvek.fx.dialog.VendorDialogImpl;
import ph.txtdis.dyvek.model.Customer;
import ph.txtdis.fx.table.AbstractNameListTable;

@Scope("prototype")
@Component("vendorTable")
public class VendorTableImpl //
		extends AbstractNameListTable<Customer, VendorDialogImpl> //
		implements VendorTable {
}
