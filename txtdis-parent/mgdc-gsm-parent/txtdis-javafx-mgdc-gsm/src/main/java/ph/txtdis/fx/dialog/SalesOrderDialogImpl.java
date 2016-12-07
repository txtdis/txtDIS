package ph.txtdis.fx.dialog;

import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import ph.txtdis.dto.BillableDetail;
import ph.txtdis.service.SalesOrderService;

@Lazy
@Component("salesOrderDialog")
public class SalesOrderDialogImpl //
		extends AbstractAllItemInCasesAndBottlesInputDialog<SalesOrderService, BillableDetail>
		implements SalesOrderDialog {
}
