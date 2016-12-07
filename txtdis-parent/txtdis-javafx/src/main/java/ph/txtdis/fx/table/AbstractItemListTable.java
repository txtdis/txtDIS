package ph.txtdis.fx.table;

import static ph.txtdis.type.Type.TEXT;

import org.springframework.beans.factory.annotation.Autowired;

import ph.txtdis.dto.Item;
import ph.txtdis.type.Type;

public abstract class AbstractItemListTable extends AbstractTableView<Item> implements ItemListTable {

	@Autowired
	private Column<Item, String> name;

	@Autowired
	private Column<Item, String> description;

	@Override
	@SuppressWarnings("unchecked")
	protected void addColumns() {
		getColumns().setAll(//
				name.ofType(Type.TEXT).width(240).build("Name", "name"), //
				description.ofType(TEXT).width(480).build("Description", "description"));
	}
}
