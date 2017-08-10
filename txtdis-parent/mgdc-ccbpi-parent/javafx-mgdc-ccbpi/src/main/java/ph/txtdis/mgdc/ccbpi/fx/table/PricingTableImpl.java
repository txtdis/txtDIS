package ph.txtdis.mgdc.ccbpi.fx.table;

import javafx.scene.control.TableColumn;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import ph.txtdis.dto.Price;
import ph.txtdis.fx.table.AppendContextMenu;
import ph.txtdis.fx.table.Column;

import java.time.LocalDate;
import java.util.List;

import static java.util.Arrays.asList;
import static ph.txtdis.type.Type.DATE;

@Scope("prototype")
@Component("pricingTable")
public class PricingTableImpl
	extends AbstractPricingTable {

	@Autowired
	private AppendContextMenu<Price> menu;

	@Autowired
	private Column<Price, LocalDate> start;

	@Override
	protected void buildColumns() {
		super.buildColumns();
		start.ofType(DATE).build("Start", "startDate");
	}

	@Override
	protected List<TableColumn<Price, ?>> columns() {
		return asList(type, price, start);
	}

	@Override
	protected void addProperties() {
		menu.addMenu(this, dialog);
	}
}
