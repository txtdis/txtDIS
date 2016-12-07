package ph.txtdis.fx.table;

import java.util.ArrayList;
import java.util.List;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javafx.scene.control.TableColumn;
import ph.txtdis.dto.BillableDetail;
import ph.txtdis.fx.dialog.NoPurchaseOrderReceiptDialog;
import ph.txtdis.service.NoPurchaseOrderReceiptService;

@Scope("prototype")
@Component("purchaseReceiptTable")
public class NoPurchaseOrderReceiptTableImpl
		extends AbstractBeverageBillableTable<NoPurchaseOrderReceiptService, NoPurchaseOrderReceiptDialog>
		implements PurchaseReceiptTable {

	@Override
	protected TableColumn<BillableDetail, ?> qtyColumn() {
		return returnedQtyInFractions;
	}

	@Override
	protected List<TableColumn<BillableDetail, ?>> columns() {
		List<TableColumn<BillableDetail, ?>> l = new ArrayList<>(super.columns());
		l.add(qtyColumn());
		return l;
	}
}
