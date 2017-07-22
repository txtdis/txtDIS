package ph.txtdis.mgdc.ccbpi.fx.dialog;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import ph.txtdis.dto.BillableDetail;
import ph.txtdis.mgdc.ccbpi.service.OrderConfirmationService;

@Scope("prototype")
@Component("orderConfirmationDialog")
public class OrderConfirmationDialogImpl //
		extends AbstractAllItemInCasesAndBottlesInputDialog<OrderConfirmationService, BillableDetail> //
		implements OrderConfirmationDialog {
}
