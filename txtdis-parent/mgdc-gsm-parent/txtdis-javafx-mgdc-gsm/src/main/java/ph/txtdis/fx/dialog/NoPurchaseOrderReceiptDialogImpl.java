package ph.txtdis.fx.dialog;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import ph.txtdis.dto.BillableDetail;
import ph.txtdis.service.NoPurchaseOrderReceiptService;

@Scope("prototype")
@Component("noPurchaseOrderReceiptDialog")
public class NoPurchaseOrderReceiptDialogImpl
		extends AbstractAllItemInCasesAndBottlesInputDialog<NoPurchaseOrderReceiptService, BillableDetail>
		implements NoPurchaseOrderReceiptDialog {
}
