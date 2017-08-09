package ph.txtdis.dyvek.fx.table;

import javafx.scene.control.TableColumn;
import org.springframework.beans.factory.annotation.Autowired;
import ph.txtdis.dyvek.model.BillableDetail;
import ph.txtdis.fx.table.AbstractTable;
import ph.txtdis.fx.table.AppTable;
import ph.txtdis.fx.table.Column;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static java.util.Arrays.asList;
import static ph.txtdis.type.Type.*;

public abstract class AbstractBillableDetailTable //
	extends AbstractTable<BillableDetail> //
	implements AppTable<BillableDetail> {

	@Autowired
	protected Column<BillableDetail, BigDecimal> qty;

	@Autowired
	private Column<BillableDetail, LocalDate> date;

	@Autowired
	private Column<BillableDetail, String> orderNo;

	@Override
	protected List<TableColumn<BillableDetail, ?>> addColumns() {
		return asList( //
			date.ofType(DATE).build("Date", "orderDate"), //
			orderNo.ofType(TEXT).width(120).build(orderNoColumnName(), "orderNo"), //
			qty.ofType(QUANTITY).build(qtyColumnName(), "qty"));
	}

	protected abstract String orderNoColumnName();

	protected abstract String qtyColumnName();
}
