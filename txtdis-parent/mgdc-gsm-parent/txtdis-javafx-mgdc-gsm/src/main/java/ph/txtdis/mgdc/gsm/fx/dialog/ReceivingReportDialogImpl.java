package ph.txtdis.mgdc.gsm.fx.dialog;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import ph.txtdis.dto.BillableDetail;
import ph.txtdis.mgdc.gsm.service.ReceivingService;

@Scope("prototype")
@Component("receivingReportDialog")
public class ReceivingReportDialogImpl //
		extends AbstractCasesAndBottlesReceivingDialog<ReceivingService, BillableDetail> //
		implements ReceivingReportDialog {
}
