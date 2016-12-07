package ph.txtdis.fx.table;

import java.util.List;

import ph.txtdis.dto.CustomerDiscount;

public interface CustomerDiscountTable extends AppTable<CustomerDiscount> {

	@Override
	AbstractCustomerDiscountTable build();

	@Override
	List<CustomerDiscount> getItems();
}