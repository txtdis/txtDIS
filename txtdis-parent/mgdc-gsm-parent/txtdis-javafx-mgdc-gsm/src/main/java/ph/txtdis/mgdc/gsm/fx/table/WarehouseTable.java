package ph.txtdis.mgdc.gsm.fx.table;

import javafx.scene.control.TableColumn;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import ph.txtdis.dto.ItemFamily;
import ph.txtdis.dto.Warehouse;
import ph.txtdis.fx.table.AbstractNameListTable;
import ph.txtdis.fx.table.Column;
import ph.txtdis.mgdc.gsm.fx.dialog.WarehouseDialog;

import java.util.ArrayList;
import java.util.List;

import static ph.txtdis.type.Type.OTHERS;

@Scope("prototype")
@Component("warehouseTable")
public class WarehouseTable
	extends AbstractNameListTable<Warehouse, WarehouseDialog> {

	@Autowired
	private Column<Warehouse, ItemFamily> family;

	@Override
	protected List<TableColumn<Warehouse, ?>> addColumns() {
		List<TableColumn<Warehouse, ?>> l = new ArrayList<>(super.addColumns());
		l.add(2, family.ofType(OTHERS).width(180).build("Item Family\nStored", "family"));
		return l;
	}
}
