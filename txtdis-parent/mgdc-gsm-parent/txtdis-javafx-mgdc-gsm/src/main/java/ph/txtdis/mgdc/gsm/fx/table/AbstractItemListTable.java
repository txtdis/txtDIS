package ph.txtdis.mgdc.gsm.fx.table;

import javafx.scene.control.TableColumn;
import org.springframework.beans.factory.annotation.Autowired;
import ph.txtdis.fx.table.AbstractTable;
import ph.txtdis.fx.table.Column;
import ph.txtdis.mgdc.gsm.dto.Item;
import ph.txtdis.type.Type;

import java.util.List;

import static java.util.Arrays.asList;
import static ph.txtdis.type.Type.TEXT;

public abstract class AbstractItemListTable
	extends AbstractTable<Item>
	implements ItemListTable {

	@Autowired
	private Column<Item, String> name;

	@Autowired
	private Column<Item, String> description;

	@Override
	protected List<TableColumn<Item, ?>> addColumns() {
		return asList( //
			name.ofType(Type.TEXT).width(240).build("Name", "name"), //
			description.ofType(TEXT).width(480).build("Description", "description"));
	}
}
