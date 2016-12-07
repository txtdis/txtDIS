package ph.txtdis.fx.dialog;

import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import ph.txtdis.dto.BillableDetail;
import ph.txtdis.service.GoodRmaReplacementService;

@Lazy
@Component("goodRmaReplacementDialog")
public class GoodRmaReplacementDialogImpl
		extends AbstractAllItemInCasesAndBottlesInputDialog<GoodRmaReplacementService, BillableDetail>
		implements GoodRmaReplacementDialog {
}
