package ph.txtdis.mgdc.gsm.fx.table;

import javafx.scene.control.TableColumn;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import ph.txtdis.fx.table.Column;
import ph.txtdis.mgdc.gsm.dto.Item;

import java.util.ArrayList;
import java.util.List;

import static ph.txtdis.type.Type.ID;

@Scope("prototype")
@Component("itemListTable")
public class ItemListTableImpl
	extends AbstractItemListTable {

	@Autowired
	private Column<Item, Long> id;

	@Override
	protected List<TableColumn<Item, ?>> addColumns() {
		List<TableColumn<Item, ?>> l = new ArrayList<>(super.addColumns());
		l.add(id.ofType(ID).build("ID No.", "id"));
		l.addAll(getColumns());
		return l;
	}
}
