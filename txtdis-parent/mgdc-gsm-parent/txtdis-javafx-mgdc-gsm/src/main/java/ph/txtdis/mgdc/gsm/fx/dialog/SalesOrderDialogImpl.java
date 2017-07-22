package ph.txtdis.mgdc.gsm.fx.dialog;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import ph.txtdis.dto.BillableDetail;
import ph.txtdis.mgdc.gsm.service.GsmBookingService;

@Scope("prototype")
@Component("salesOrderDialog")
public class SalesOrderDialogImpl //
		extends AbstractAllItemInCasesAndBottlesInputDialog<GsmBookingService, BillableDetail>
		implements SalesOrderDialog {
}
