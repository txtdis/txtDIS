package ph.txtdis.fx.dialog;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import ph.txtdis.dto.BillableDetail;
import ph.txtdis.service.LoadManifestService;

@Scope("prototype")
@Component("loadManifestDialog")
public class LoadManifestDialogImpl //
		extends AbstractAllItemInCasesAndBottlesInputDialog<LoadManifestService, BillableDetail>
		implements LoadManifestDialog {
}
