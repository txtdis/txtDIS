package ph.txtdis.mgdc.ccbpi.fx.table;

import javafx.scene.control.TableColumn;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import ph.txtdis.fx.table.Column;
import ph.txtdis.mgdc.ccbpi.dto.Item;
import ph.txtdis.type.Type;

import java.util.ArrayList;
import java.util.List;

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
