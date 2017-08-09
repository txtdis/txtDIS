package ph.txtdis.mgdc.gsm.fx.table;

import javafx.scene.control.TableColumn;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import ph.txtdis.dto.Price;

import java.util.ArrayList;
import java.util.List;

import static java.util.Arrays.asList;

@Scope("prototype")
@Component("pricingTable")
public class PricingTableImpl //
	extends AbstractPricingTable {

	@Override
	protected List<TableColumn<Price, ?>> columns() {
		List<TableColumn<Price, ?>> l = new ArrayList<>(asList(type, price));
		l.addAll(decisionNeeded.addColumns());
		return l;
	}
}
