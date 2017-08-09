package ph.txtdis.dyvek.fx.table;

import javafx.scene.control.TableColumn;
import org.springframework.beans.factory.annotation.Autowired;
import ph.txtdis.app.DialogClosingOnlyApp;
import ph.txtdis.dyvek.model.Billable;
import ph.txtdis.fx.table.AbstractTable;
import ph.txtdis.fx.table.AppTable;
import ph.txtdis.fx.table.Column;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static java.util.Arrays.asList;
import static ph.txtdis.type.Type.*;

public abstract class AbstractOrderListTable //
	extends AbstractTable<Billable> //
	implements AppTable<Billable> {

	@Autowired
	protected Column<Billable, BigDecimal> price;

	@Autowired
	protected DialogClosingOnlyApp app;

	@Autowired
	private Column<Billable, LocalDate> orderDate;

	@Autowired
	private Column<Billable, String> customer, orderNo, item;

	@Autowired
	private Column<Billable, BigDecimal> qty;

	@Override
	protected List<TableColumn<Billable, ?>> addColumns() {
		return asList( //
			orderDate.ofType(DATE).launches(app).build(dateColumnName() + " Date", "orderDate"), //
			customer.ofType(TEXT).launches(app).build(customerColumnName(), customerGetterName()), //
			orderNo.ofType(TEXT).launches(app).width(90).build(orderNoColumnName(), orderNoGetterName()), //
			item.ofType(TEXT).launches(app).width(90).build("Item", "item"), //
			price.ofType(CURRENCY).launches(app).width(90).build("Price", "priceValue"), //
			qty.ofType(QUANTITY).launches(app).width(90).build(qtyColumnName(), qtyGetterName()));
	}

	protected abstract String dateColumnName();

	protected abstract String customerColumnName();

	protected abstract String customerGetterName();

	protected abstract String orderNoColumnName();

	protected abstract String orderNoGetterName();

	protected abstract String qtyColumnName();

	protected abstract String qtyGetterName();
}
