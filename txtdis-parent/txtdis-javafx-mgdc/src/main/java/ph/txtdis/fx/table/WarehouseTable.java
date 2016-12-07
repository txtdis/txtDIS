package ph.txtdis.fx.table;

import static ph.txtdis.type.Type.OTHERS;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import ph.txtdis.dto.ItemFamily;
import ph.txtdis.dto.Warehouse;
import ph.txtdis.fx.dialog.WarehouseDialog;

@Scope("prototype")
@Component("warehouseTable")
public class WarehouseTable extends AbstractNameListTable<Warehouse, WarehouseDialog> {

	@Autowired
	private Column<Warehouse, ItemFamily> family;

	@Override
	protected void addColumns() {
		super.addColumns();
		getColumns().add(2, family.ofType(OTHERS).width(180).build("Item Family\nStored", "family"));
	}
}
