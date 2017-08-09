package ph.txtdis.mgdc.gsm.fx.dialog;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import ph.txtdis.dto.BillableDetail;
import ph.txtdis.mgdc.fx.dialog.BadRmaDialog;
import ph.txtdis.mgdc.gsm.service.BadRmaService;

@Scope("prototype")
@Component("badRmaDialog")
public class BadRmaDialogImpl //
	extends AbstractAllItemInCasesAndBottlesInputDialog<BadRmaService, BillableDetail> //
	implements BadRmaDialog {
}
