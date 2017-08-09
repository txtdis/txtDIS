package ph.txtdis.mgdc.fx.table;

import javafx.scene.control.TableColumn;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import ph.txtdis.dto.Inventory;
import ph.txtdis.fx.table.AbstractTable;
import ph.txtdis.fx.table.Column;
import ph.txtdis.mgdc.app.LaunchableItemApp;

import java.math.BigDecimal;
import java.util.List;

import static java.util.Arrays.asList;
import static ph.txtdis.type.Type.*;

@Scope("prototype")
@Component("inventoryTable")
public class InventoryTable
	extends AbstractTable<Inventory> {

	@Autowired
	private LaunchableItemApp app;

	@Autowired
	private Column<Inventory, Long> id;

	@Autowired
	private Column<Inventory, String> item;

	@Autowired
	private Column<Inventory, BigDecimal> goodQty, badQty, value, obsolescenceValue;

	@Autowired
	private Column<Inventory, Integer> daysLevel;

	@Override
	protected List<TableColumn<Inventory, ?>> addColumns() {
		return asList( //
			id.launches(app).ofType(ID).build("ID", "id"), //
			item.launches(app).ofType(TEXT).width(180).build("Item", "item"), //
			goodQty.launches(app).ofType(QUANTITY).build("Good Quantity", "goodQty"), //
			badQty.launches(app).ofType(QUANTITY).build("Bad Quantity", "badQty"), //
			daysLevel.launches(app).ofType(INTEGER).width(80).build("Days Level", "daysLevel"), //
			value.launches(app).ofType(CURRENCY).build("Value", "value"), //
			obsolescenceValue.launches(app).ofType(CURRENCY).build("Allowance for Obsolescence", "obsolesenceValue"));
	}
}
