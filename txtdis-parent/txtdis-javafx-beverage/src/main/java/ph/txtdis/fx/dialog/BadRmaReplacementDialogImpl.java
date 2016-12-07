package ph.txtdis.fx.dialog;

import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import ph.txtdis.dto.BillableDetail;
import ph.txtdis.service.BadRmaReplacementService;

@Lazy
@Component("badRmaReplacementDialog")
public class BadRmaReplacementDialogImpl
		extends AbstractAllItemInCasesAndBottlesInputDialog<BadRmaReplacementService, BillableDetail>
		implements BadRmaReplacementDialog {
}
