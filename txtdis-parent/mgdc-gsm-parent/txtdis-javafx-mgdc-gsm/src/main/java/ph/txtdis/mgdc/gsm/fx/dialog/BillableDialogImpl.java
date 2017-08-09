package ph.txtdis.mgdc.gsm.fx.dialog;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import ph.txtdis.dto.BillableDetail;
import ph.txtdis.mgdc.fx.dialog.BillableDialog;
import ph.txtdis.mgdc.gsm.service.BillableService;

@Scope("prototype")
@Component("billableDialog")
public class BillableDialogImpl //
	extends AbstractAllItemInCasesAndBottlesInputDialog<BillableService, BillableDetail> //
	implements BillableDialog {
}
