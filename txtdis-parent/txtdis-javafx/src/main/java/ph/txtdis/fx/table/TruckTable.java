package ph.txtdis.fx.table;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import ph.txtdis.dto.Truck;
import ph.txtdis.fx.dialog.TruckDialog;

@Scope("prototype")
@Component("truckTable")
public class TruckTable extends AbstractNameListTable<Truck, TruckDialog> {
}
