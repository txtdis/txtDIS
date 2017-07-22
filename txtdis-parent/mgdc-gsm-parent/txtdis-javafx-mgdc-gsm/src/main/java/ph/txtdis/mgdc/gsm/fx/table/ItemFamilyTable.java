package ph.txtdis.mgdc.gsm.fx.table;

import static ph.txtdis.type.Type.TEXT;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javafx.scene.control.TableColumn;
import ph.txtdis.dto.ItemFamily;
import ph.txtdis.fx.table.AbstractNameListTable;
import ph.txtdis.fx.table.Column;
import ph.txtdis.mgdc.gsm.fx.dialog.ItemFamilyDialog;
import ph.txtdis.type.ItemTier;

@Scope("prototype")
@Component("itemFamilyTable")
public class ItemFamilyTable extends AbstractNameListTable<ItemFamily, ItemFamilyDialog> {

	@Autowired
	private Column<ItemFamily, ItemTier> tier;

	@Override
	protected List<TableColumn<ItemFamily, ?>> addColumns() {
		List<TableColumn<ItemFamily, ?>> l = new ArrayList<>(super.addColumns());
		l.add(2, tier.ofType(TEXT).width(120).build("Tier", "tier"));
		return l;
	}
}
