package ph.txtdis.dyvek.app;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import ph.txtdis.app.AbstractTableApp;
import ph.txtdis.app.ItemApp;
import ph.txtdis.dyvek.fx.table.ItemTableImpl;
import ph.txtdis.dyvek.model.Item;
import ph.txtdis.dyvek.service.ItemService;

@Scope("prototype")
@Component("itemApp")
public class ItemAppImpl
	extends AbstractTableApp<ItemTableImpl, ItemService, Item>
	implements ItemApp {
}
