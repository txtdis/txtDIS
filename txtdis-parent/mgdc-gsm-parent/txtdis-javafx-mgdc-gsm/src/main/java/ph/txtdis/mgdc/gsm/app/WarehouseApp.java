package ph.txtdis.mgdc.gsm.app;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import ph.txtdis.app.AbstractTableApp;
import ph.txtdis.dto.Warehouse;
import ph.txtdis.mgdc.gsm.fx.table.WarehouseTable;
import ph.txtdis.mgdc.gsm.service.WarehouseService;

@Scope("prototype")
@Component("warehouseApp")
public class WarehouseApp
	extends AbstractTableApp<WarehouseTable, WarehouseService, Warehouse> {
}
