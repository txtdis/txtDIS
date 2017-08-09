package ph.txtdis.mgdc.gsm.fx.table;

import ph.txtdis.fx.table.AppTable;
import ph.txtdis.mgdc.gsm.dto.CustomerDiscount;

import java.util.List;

public interface CustomerDiscountTable
	extends AppTable<CustomerDiscount> {

	@Override
	AbstractCustomerDiscountTable build();

	@Override
	List<CustomerDiscount> getItems();
}