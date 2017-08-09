package ph.txtdis.dyvek.fx.table;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import ph.txtdis.dyvek.fx.dialog.ItemDialogImpl;
import ph.txtdis.dyvek.model.Item;
import ph.txtdis.fx.table.AbstractNameListTable;

@Scope("prototype")
@Component("itemTable")
public class ItemTableImpl
	extends AbstractNameListTable<Item, ItemDialogImpl> {
}
