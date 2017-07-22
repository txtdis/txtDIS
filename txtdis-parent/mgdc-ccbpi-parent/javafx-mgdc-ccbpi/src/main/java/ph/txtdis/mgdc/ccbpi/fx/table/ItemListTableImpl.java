package ph.txtdis.mgdc.ccbpi.fx.table;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javafx.scene.control.TableColumn;
import ph.txtdis.fx.table.Column;
import ph.txtdis.mgdc.ccbpi.dto.Item;
import ph.txtdis.type.Type;

@Scope("prototype")
@Component("itemListTable")
public class ItemListTableImpl //
		extends AbstractItemListTable {

	@Autowired
	private Column<Item, String> id;

	@Override
	protected List<TableColumn<Item, ?>> addColumns() {
		List<TableColumn<Item, ?>> l = new ArrayList<>(super.addColumns());
		l.add(id.ofType(Type.CODE).build("ID No.", "vendorNo"));
		l.addAll(getColumns());
		return l;
	}
}
