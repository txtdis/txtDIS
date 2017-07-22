package ph.txtdis.mgdc.gsm.fx.table;

import java.util.ArrayList;
import java.util.List;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javafx.scene.control.TableColumn;
import ph.txtdis.dto.BillableDetail;
import ph.txtdis.mgdc.gsm.fx.dialog.NoPurchaseOrderReceiptDialog;
import ph.txtdis.mgdc.gsm.service.NoPurchaseOrderReceiptService;

@Scope("prototype")
@Component("purchaseReceiptTable")
public class NoPurchaseOrderReceiptTableImpl //
		extends AbstractBeverageBillableTable<NoPurchaseOrderReceiptService, NoPurchaseOrderReceiptDialog> //
		implements PurchaseReceiptTable {

	@Override
	protected TableColumn<BillableDetail, ?> qtyColumn() {
		return returnedQtyInFractions;
	}

	@Override
	protected List<TableColumn<BillableDetail, ?>> addColumns() {
		List<TableColumn<BillableDetail, ?>> l = new ArrayList<>(super.addColumns());
		l.add(qtyColumn());
		return l;
	}
}
