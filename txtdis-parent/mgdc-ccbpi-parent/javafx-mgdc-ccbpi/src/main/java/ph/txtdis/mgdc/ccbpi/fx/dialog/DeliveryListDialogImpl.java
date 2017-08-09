package ph.txtdis.mgdc.ccbpi.fx.dialog;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import ph.txtdis.dto.BillableDetail;
import ph.txtdis.mgdc.ccbpi.service.DeliveryListService;

@Scope("prototype")
@Component("deliveryListDialog")
public class DeliveryListDialogImpl //
	extends AbstractAllItemInCasesAndBottlesInputDialog<DeliveryListService, BillableDetail>
	implements DeliveryListDialog {
}
