package ph.txtdis.mgdc.gsm.fx.table;

import static java.util.Arrays.asList;
import static ph.txtdis.type.Type.CURRENCY;
import static ph.txtdis.type.Type.OTHERS;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javafx.scene.control.TableColumn;
import ph.txtdis.fx.table.Column;
import ph.txtdis.mgdc.gsm.dto.CustomerDiscount;
import ph.txtdis.mgdc.gsm.dto.Item;

@Scope("prototype")
@Component("customerDiscountTable")
public class CustomerDiscountTableImpl extends AbstractCustomerDiscountTable {

	@Autowired
	private Column<CustomerDiscount, Item> item;

	@Override
	protected List<TableColumn<CustomerDiscount, ?>> addColumns() {
		List<TableColumn<CustomerDiscount, ?>> l = new ArrayList<>(asList( //
				item.ofType(OTHERS).width(140).build("Limited\nto", "item"), //
				discount.ofType(CURRENCY).build("Discount", "discount")));
		l.addAll(decisionNeeded.addColumns());
		return l;
	}
}
