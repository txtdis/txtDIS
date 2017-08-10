package ph.txtdis.mgdc.ccbpi.fx.table;

import javafx.scene.control.TableColumn;
import org.apache.commons.lang3.math.Fraction;
import org.springframework.beans.factory.annotation.Autowired;
import ph.txtdis.app.LaunchableApp;
import ph.txtdis.dto.SalesItemVariance;
import ph.txtdis.fx.table.AbstractTable;
import ph.txtdis.fx.table.Column;

import java.math.BigDecimal;
import java.util.List;

import static java.util.Arrays.asList;
import static ph.txtdis.type.Type.*;

public abstract class AbstractOrderListTable<App extends LaunchableApp> // 
	extends AbstractTable<SalesItemVariance> {

	@Autowired
	private Column<SalesItemVariance, String> orderNo, customer;

	@Autowired
	private Column<SalesItemVariance, Fraction> qty;

	@Autowired
	private Column<SalesItemVariance, BigDecimal> value;

	@Autowired
	private App app;

	@Override
	protected List<TableColumn<SalesItemVariance, ?>> addColumns() {
		return asList( //
			orderNo.ofType(TEXT).launches(app).width(180).build("Order No.", "orderNo"), //
			customer.ofType(TEXT).launches(app).width(240).build("Outlet", "customer"), //
			qty.ofType(FRACTION).launches(app).build("Quantity", qtyAndValuePrefix() + "QtyInFractions"), //
			value.ofType(CURRENCY).launches(app).build("Amount", qtyAndValuePrefix() + "Value"));
	}

	protected abstract String qtyAndValuePrefix();
}
