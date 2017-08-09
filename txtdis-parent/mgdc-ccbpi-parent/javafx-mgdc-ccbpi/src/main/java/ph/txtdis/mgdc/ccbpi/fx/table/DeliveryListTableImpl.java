package ph.txtdis.mgdc.ccbpi.fx.table;

import static java.util.Arrays.asList;

import java.util.List;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javafx.scene.control.TableColumn;
import ph.txtdis.dto.BillableDetail;
import ph.txtdis.mgdc.ccbpi.fx.dialog.DeliveryListDialog;
import ph.txtdis.mgdc.ccbpi.service.DeliveryListService;

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
