package ph.txtdis.fx.table;

import static java.util.Arrays.asList;

import java.util.List;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javafx.scene.control.TableColumn;
import ph.txtdis.dto.BillableDetail;
import ph.txtdis.fx.dialog.BillableDialog;
import ph.txtdis.service.CokeBillableService;

@Scope("Lazy")
@Component("orderConfirmationBillableTable")
public class OrderConfirmationBillableTableImpl
		extends AbstractBeverageBillableTable<CokeBillableService, BillableDialog> {

	@Override
	protected List<TableColumn<BillableDetail, ?>> columns() {
		return asList(itemVendorId, name, quality, uom, price, bookedQtyInFractions, subtotal);
	}
}
