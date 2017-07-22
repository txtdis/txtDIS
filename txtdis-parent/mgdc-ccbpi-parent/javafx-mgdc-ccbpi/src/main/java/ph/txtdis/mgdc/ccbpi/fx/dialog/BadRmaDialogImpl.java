package ph.txtdis.mgdc.ccbpi.fx.dialog;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import ph.txtdis.dto.BillableDetail;
import ph.txtdis.mgdc.ccbpi.service.BadRmaService;
import ph.txtdis.mgdc.fx.dialog.BadRmaDialog;

@Scope("prototype")
@Component("badRmaDialog")
public class BadRmaDialogImpl //
		extends AbstractAllItemInCasesAndBottlesInputDialog<BadRmaService, BillableDetail> //
		implements BadRmaDialog {
}
