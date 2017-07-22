package ph.txtdis.mgdc.gsm.fx.table;

import static java.util.Arrays.asList;

import java.util.ArrayList;
import java.util.List;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javafx.scene.control.TableColumn;
import ph.txtdis.dto.Price;

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
