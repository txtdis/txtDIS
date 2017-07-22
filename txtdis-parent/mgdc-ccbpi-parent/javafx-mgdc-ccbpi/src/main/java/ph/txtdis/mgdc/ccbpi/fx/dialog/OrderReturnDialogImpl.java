package ph.txtdis.mgdc.ccbpi.fx.dialog;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import ph.txtdis.dto.BillableDetail;
import ph.txtdis.mgdc.ccbpi.service.OrderReturnService;

@Scope("prototype")
@Component("orderReturnDialog")
public class OrderReturnDialogImpl //
		extends AbstractUpToReturnableQtyCasesAndBottlesReceivingDialog<OrderReturnService, BillableDetail> //
		implements OrderReturnDialog {
}
