package ph.txtdis.dyvek.fx.table;

import javafx.scene.control.TableColumn;
import org.springframework.beans.factory.annotation.Autowired;
import ph.txtdis.dyvek.model.Aging;
import ph.txtdis.fx.table.AbstractTable;
import ph.txtdis.fx.table.Column;

import java.math.BigDecimal;
import java.util.List;

import static java.util.Arrays.asList;
import static ph.txtdis.type.Type.*;

public abstract class AbstractAgingTable //
	extends AbstractTable<Aging> {

	@Autowired
	private Column<Aging, String> orderNo;

	@Autowired
	private Column<Aging, Long> date;

	@Autowired
	private Column<Aging, BigDecimal> qty;

	@Override
	protected List<TableColumn<Aging, ?>> addColumns() {
		return asList( //
			orderNo.ofType(TEXT).width(120).build(customerColumnName(), "customer"), //
			date.ofType(INTEGER).build("Days Over", "daysOver"), //
			qty.ofType(CURRENCY).build("Total Amount", "value"));
	}

	protected abstract String customerColumnName();
}
