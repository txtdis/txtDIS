package ph.txtdis.mgdc.gsm.fx.table;

import javafx.scene.control.TableColumn;
import org.springframework.beans.factory.annotation.Autowired;
import ph.txtdis.app.CustomerReceivableApp;
import ph.txtdis.dto.AgingReceivable;
import ph.txtdis.fx.table.AbstractTable;
import ph.txtdis.fx.table.Column;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import static ph.txtdis.type.Type.CURRENCY;
import static ph.txtdis.type.Type.TEXT;

public abstract class AbstractAgingReceivableTable //
	extends AbstractTable<AgingReceivable> //
	implements AgingReceivableTable {

	@Autowired
	protected Column<AgingReceivable, String> customer;

	@Autowired
	protected CustomerReceivableApp customerReceivableApp;

	@Autowired
	private Column<AgingReceivable, BigDecimal> oneToSeven, eightToFifteen, sixteenToThirty, moreThanThirty, aging,
		total;

	@Override
	protected List<TableColumn<AgingReceivable, ?>> addColumns() {
		return Arrays.asList(//
			customerColumn(), //
			oneToSeven.launches(customerReceivableApp).ofType(CURRENCY).build("1-7", "oneToSevenValue"),
			eightToFifteen.launches(customerReceivableApp).ofType(CURRENCY).build("8-15", "eightToFifteenValue"),
			sixteenToThirty.launches(customerReceivableApp).ofType(CURRENCY).build("16-30", "sixteenToThirtyValue"),
			moreThanThirty.launches(customerReceivableApp).ofType(CURRENCY).build(">30", "greaterThanThirtyValue"),
			total.launches(customerReceivableApp).ofType(CURRENCY).build("All Overdue", "agingValue"),
			aging.launches(customerReceivableApp).ofType(CURRENCY).build("All Aging", "totalValue"));
	}

	protected Column<AgingReceivable, String> customerColumn() {
		return customer.ofType(TEXT).width(240).build("Customer", "customer");
	}
}