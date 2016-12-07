package ph.txtdis.fx.dialog;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import ph.txtdis.dto.BillableDetail;
import ph.txtdis.service.BillableService;

@Scope("prototype")
@Component("billableDialog")
public class BillableDialogImpl extends AbstractAllItemInCasesAndBottlesInputDialog<BillableService, BillableDetail>
		implements BillableDialog {
}
