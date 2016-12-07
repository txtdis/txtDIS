package ph.txtdis.fx.table;

import static ph.txtdis.type.Type.CURRENCY;
import static ph.txtdis.type.Type.OTHERS;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import ph.txtdis.dto.CustomerDiscount;
import ph.txtdis.dto.Item;

@Scope("prototype")
@Component("customerDiscountTable")
public class CustomerDiscountTableImpl extends AbstractCustomerDiscountTable {

	@Autowired
	private Column<CustomerDiscount, Item> item;

	@Override
	@SuppressWarnings("unchecked")
	protected void addColumns() {
		getColumns().setAll(//
				item.ofType(OTHERS).width(140).build("Limited\nto", "item"), //
				discount.ofType(CURRENCY).build("Discount", "discount"));
		getColumns().addAll(decisionNeeded.addColumns());
	}
}
