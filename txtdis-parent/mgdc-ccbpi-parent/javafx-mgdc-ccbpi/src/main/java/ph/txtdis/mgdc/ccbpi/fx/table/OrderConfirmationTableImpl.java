package ph.txtdis.mgdc.ccbpi.fx.table;

import static java.util.Arrays.asList;

import java.util.List;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javafx.scene.control.TableColumn;
import ph.txtdis.dto.BillableDetail;
import ph.txtdis.mgdc.ccbpi.fx.dialog.OrderConfirmationDialog;
import ph.txtdis.mgdc.ccbpi.service.OrderConfirmationService;

@Scope("prototype")
@Component("orderConfirmationTable")
public class OrderConfirmationTableImpl //
	extends AbstractBeverageBillableTable<OrderConfirmationService, OrderConfirmationDialog> //
	implements OrderConfirmationTable {

	@Override
	protected List<TableColumn<BillableDetail, ?>> addColumns() {
		super.addColumns();
		return asList(itemVendorId, name, quality, uom, price, bookedQtyInFractions, subtotal);
	}

	@Override
	protected String subtotal() {
		return "initialSubtotalValue";
	}
}
