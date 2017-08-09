package ph.txtdis.mgdc.gsm.fx.table;

import javafx.scene.control.TableColumn;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import ph.txtdis.dto.BillableDetail;
import ph.txtdis.mgdc.gsm.fx.dialog.NoPurchaseOrderReceiptDialog;
import ph.txtdis.mgdc.gsm.service.NoPurchaseOrderReceiptService;

import java.util.ArrayList;
import java.util.List;

@Scope("prototype")
@Component("purchaseReceiptTable")
public class NoPurchaseOrderReceiptTableImpl //
	extends AbstractBeverageBillableTable<NoPurchaseOrderReceiptService, NoPurchaseOrderReceiptDialog> //
	implements PurchaseReceiptTable {

	@Override
	protected List<TableColumn<BillableDetail, ?>> addColumns() {
		List<TableColumn<BillableDetail, ?>> l = new ArrayList<>(super.addColumns());
		l.add(qtyColumn());
		return l;
	}

	@Override
	protected TableColumn<BillableDetail, ?> qtyColumn() {
		return returnedQtyInFractions;
	}
}
