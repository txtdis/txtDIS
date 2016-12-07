package ph.txtdis.fx.table;

import static ph.txtdis.type.Type.INTEGER;
import static ph.txtdis.type.Type.OTHERS;
import static ph.txtdis.type.Type.PERCENT;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import ph.txtdis.dto.CustomerDiscount;
import ph.txtdis.dto.ItemFamily;

@Scope("prototype")
@Component("customerDiscountTable")
public class CustomerDiscountTableImpl extends AbstractCustomerDiscountTable {

	@Autowired
	private Column<CustomerDiscount, Integer> level;

	@Autowired
	private Column<CustomerDiscount, ItemFamily> familyLimit;

	@Override
	@SuppressWarnings("unchecked")
	protected void addColumns() {
		getColumns().setAll(//
				level.ofType(INTEGER).width(60).build("Level", "level"),
				discount.ofType(PERCENT).build("Discount", "discount"),
				familyLimit.ofType(OTHERS).build("Limited\nto", "familyLimit"));
		getColumns().addAll(decisionNeeded.addColumns());
	}
}
