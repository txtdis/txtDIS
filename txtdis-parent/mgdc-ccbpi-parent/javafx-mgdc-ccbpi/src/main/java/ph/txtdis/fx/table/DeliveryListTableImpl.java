package ph.txtdis.fx.table;

import static java.util.Arrays.asList;

import java.util.List;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javafx.scene.control.TableColumn;
import ph.txtdis.dto.BillableDetail;
import ph.txtdis.fx.dialog.DeliveryListDialog;
import ph.txtdis.service.DeliveryListService;

@Scope("prototype")
@Component("deliveryListTable")
public class DeliveryListTableImpl //
		extends AbstractBeverageBillableTable<DeliveryListService, DeliveryListDialog> //
		implements DeliveryListTable {

	@Override
	protected List<TableColumn<BillableDetail, ?>> columns() {
		return asList(itemVendorId, name, quality, uom, price, qtyColumn(), subtotal);
	}
}
