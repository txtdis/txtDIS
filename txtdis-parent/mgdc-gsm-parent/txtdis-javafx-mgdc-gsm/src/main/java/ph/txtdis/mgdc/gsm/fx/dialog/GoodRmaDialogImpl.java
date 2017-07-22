package ph.txtdis.mgdc.gsm.fx.dialog;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import ph.txtdis.dto.BillableDetail;
import ph.txtdis.mgdc.fx.dialog.GoodRmaDialog;
import ph.txtdis.mgdc.gsm.service.GoodRefundedRmaService;

@Scope("prototype")
@Component("goodRmaDialog")
public class GoodRmaDialogImpl //
		extends AbstractAllItemInCasesAndBottlesInputDialog<GoodRefundedRmaService, BillableDetail> //
		implements GoodRmaDialog {
}
