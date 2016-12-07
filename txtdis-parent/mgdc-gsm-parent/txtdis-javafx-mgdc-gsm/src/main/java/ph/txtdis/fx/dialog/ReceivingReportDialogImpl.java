package ph.txtdis.fx.dialog;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import ph.txtdis.dto.BillableDetail;
import ph.txtdis.service.ReceivingService;

@Scope("prototype")
@Component("receivingReportDialog")
public class ReceivingReportDialogImpl extends AbstractCasesAndBottlesReceivingDialog<ReceivingService, BillableDetail>
		implements ReceivingReportDialog {
}
