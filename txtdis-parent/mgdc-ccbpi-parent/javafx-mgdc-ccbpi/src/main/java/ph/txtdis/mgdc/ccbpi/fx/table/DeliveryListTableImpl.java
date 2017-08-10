package ph.txtdis.mgdc.ccbpi.fx.table;

import javafx.scene.control.TableColumn;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import ph.txtdis.dto.BillableDetail;
import ph.txtdis.mgdc.ccbpi.fx.dialog.DeliveryListDialog;
import ph.txtdis.mgdc.ccbpi.service.DeliveryListService;

import java.util.List;

import static java.util.Arrays.asList;

@Scope("prototype")
@Component("deliveryListTable")
public class DeliveryListTableImpl //
	extends AbstractBeverageBillableTable<DeliveryListService, DeliveryListDialog> //
	implements DeliveryListTable {

	@Override
	protected List<TableColumn<BillableDetail, ?>> addColumns() {
		super.addColumns();
		return asList(itemVendorId, name, quality, uom, price, qtyColumn(), subtotal);
	}
}
