package ph.txtdis.mgdc.gsm.app;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import ph.txtdis.app.AbstractTableApp;
import ph.txtdis.dto.ItemFamily;
import ph.txtdis.mgdc.gsm.fx.table.ItemFamilyTable;
import ph.txtdis.mgdc.gsm.service.ItemFamilyService;

@Scope("prototype")
@Component("itemFamilyApp")
public class ItemFamilyApp extends AbstractTableApp<ItemFamilyTable, ItemFamilyService, ItemFamily> {
}
