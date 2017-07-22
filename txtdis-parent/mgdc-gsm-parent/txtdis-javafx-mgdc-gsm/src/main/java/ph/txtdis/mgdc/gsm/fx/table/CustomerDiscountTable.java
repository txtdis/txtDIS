package ph.txtdis.mgdc.gsm.fx.table;

import java.util.List;

import ph.txtdis.fx.table.AppTable;
import ph.txtdis.mgdc.gsm.dto.CustomerDiscount;

public interface CustomerDiscountTable extends AppTable<CustomerDiscount> {

	@Override
	AbstractCustomerDiscountTable build();

	@Override
	List<CustomerDiscount> getItems();
}