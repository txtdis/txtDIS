package ph.txtdis.app;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import ph.txtdis.dto.Truck;
import ph.txtdis.fx.table.TruckTable;
import ph.txtdis.service.TruckService;

@Scope("prototype")
@Component("truckApp")
public class TruckApp extends AbstractTableApp<TruckTable, TruckService, Truck> {
}
