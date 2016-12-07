package ph.txtdis.fx.table;

import static ph.txtdis.type.Type.ID;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javafx.scene.control.TableColumn;
import ph.txtdis.dto.Item;

@Scope("prototype")
@Component("itemListTable")
public class ItemListTableImpl extends AbstractItemListTable {

	@Autowired
	private Column<Item, Long> id;

	@Override
	protected void addColumns() {
		super.addColumns();
		List<TableColumn<Item, ?>> l = new ArrayList<>();
		l.add(id.ofType(ID).build("ID No.", "id"));
		l.addAll(getColumns());
		getColumns().setAll(l);
	}
}
