package ph.txtdis.mgdc.ccbpi.fx.table;

import static java.util.Arrays.asList;

import java.util.List;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javafx.scene.control.TableColumn;
import ph.txtdis.dto.BillableDetail;
import ph.txtdis.mgdc.ccbpi.fx.dialog.OrderReturnDialog;
import ph.txtdis.mgdc.ccbpi.service.OrderReturnService;

@Scope("prototype")
@Component("orderReturnTable")
public class OrderReturnTableImpl //
		extends AbstractBeverageBillableTable<OrderReturnService, OrderReturnDialog> //
		implements OrderReturnTable {

	@Override
	protected List<TableColumn<BillableDetail, ?>> addColumns() {
		super.addColumns();
		return asList(itemVendorId, name, quality, uom, returnedQtyInFractions, subtotal);
	}

	@Override
	protected String subtotal() {
		return "returnedSubtotalValue";
	}
}
