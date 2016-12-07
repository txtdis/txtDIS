package ph.txtdis.fx.dialog;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import ph.txtdis.dto.BillableDetail;
import ph.txtdis.service.CokeBillableService;

@Scope("prototype")
@Component("billableDialog")
public class BillableDialogImpl extends AbstractAllItemInCasesAndBottlesInputDialog<CokeBillableService, BillableDetail>
		implements BillableDialog {
}
