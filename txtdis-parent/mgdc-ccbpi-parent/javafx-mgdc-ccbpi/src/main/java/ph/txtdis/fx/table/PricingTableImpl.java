package ph.txtdis.fx.table;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javafx.collections.ObservableList;
import javafx.scene.control.TableColumn;
import ph.txtdis.dto.Price;

@Scope("prototype")
@Component("pricingTable")
public class PricingTableImpl extends AbstractPricingTable {

	@Override
	protected void addColumns() {
		super.addColumns();
		ObservableList<TableColumn<Price, ?>> columns = getColumns();
		columns.remove(channelLimit);
		columns.removeAll(decisionNeeded.getApprovalColumns());
	}
}
