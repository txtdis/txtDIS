package ph.txtdis.mgdc.gsm.fx.dialog;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import ph.txtdis.dto.BillableDetail;
import ph.txtdis.mgdc.gsm.service.NoPurchaseOrderReceiptService;

@Scope("prototype")
@Component("noPurchaseOrderReceiptDialog")
public class NoPurchaseOrderReceiptDialogImpl //
		extends AbstractAllItemInCasesAndBottlesInputDialog<NoPurchaseOrderReceiptService, BillableDetail> //
		implements NoPurchaseOrderReceiptDialog {
}
