package ph.txtdis.mgdc.ccbpi.fx.dialog;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import ph.txtdis.dto.PickListDetail;
import ph.txtdis.mgdc.ccbpi.service.LoadReturnService;

@Scope("prototype")
@Component("loadReturnDialog")
public class LoadReturnDialogImpl //
	extends AbstractUpToReturnableQtyCasesAndBottlesReceivingDialog<LoadReturnService, PickListDetail> //
	implements LoadReturnDialog {
}
