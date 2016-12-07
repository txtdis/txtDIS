package ph.txtdis.fx.table;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import javafx.scene.control.TableColumn;
import ph.txtdis.dto.Item;
import ph.txtdis.type.Type;

@Lazy
@Component("itemListTable")
public class ItemListTableImpl extends AbstractItemListTable {

	@Autowired
	private Column<Item, String> id;

	@Override
	protected void addColumns() {
		super.addColumns();
		List<TableColumn<Item, ?>> l = new ArrayList<>();
		l.add(id.ofType(Type.CODE).build("ID No.", "vendorId"));
		l.addAll(getColumns());
		getColumns().setAll(l);
	}
}
