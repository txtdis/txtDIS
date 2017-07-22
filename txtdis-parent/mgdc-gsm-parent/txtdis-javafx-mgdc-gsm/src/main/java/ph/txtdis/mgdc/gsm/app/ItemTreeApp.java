package ph.txtdis.mgdc.gsm.app;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import ph.txtdis.app.AbstractTableApp;
import ph.txtdis.dto.ItemTree;
import ph.txtdis.mgdc.gsm.fx.table.ItemTreeTable;
import ph.txtdis.mgdc.gsm.service.ItemTreeService;

@Scope("prototype")
@Component("itemTreeApp")
public class ItemTreeApp extends AbstractTableApp<ItemTreeTable, ItemTreeService, ItemTree> {
}
