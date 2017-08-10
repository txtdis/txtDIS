package ph.txtdis.mgdc.ccbpi.fx.dialog;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import ph.txtdis.dto.BillableDetail;
import ph.txtdis.mgdc.ccbpi.service.LoadManifestService;

@Scope("prototype")
@Component("loadManifestDialog")
public class LoadManifestDialogImpl //
	extends AbstractAllItemInCasesAndBottlesInputDialog<LoadManifestService, BillableDetail> //
	implements LoadManifestDialog {
}
