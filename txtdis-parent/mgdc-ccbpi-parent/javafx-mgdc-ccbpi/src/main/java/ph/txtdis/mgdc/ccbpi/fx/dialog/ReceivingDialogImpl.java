package ph.txtdis.mgdc.ccbpi.fx.dialog;

import java.util.List;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import ph.txtdis.dto.BillableDetail;
import ph.txtdis.fx.control.InputNode;
import ph.txtdis.mgdc.ccbpi.service.CokeBillableService;
import ph.txtdis.mgdc.fx.dialog.BillableDialog;

@Scope("prototype")
@Component("receivingDialog")
public class ReceivingDialogImpl //
	extends AbstractCasesAndBottlesReceivingDialog<CokeBillableService, BillableDetail> //
	implements BillableDialog {

	@Override
	protected List<InputNode<?>> quantitiesInCasesAndBottlesInputs() {
		List<InputNode<?>> l = super.quantitiesInCasesAndBottlesInputs();
		quantitiesInCasesAndBottlesDialog.setQtyFieldOnAction(e -> acceptOnlyUpToBookedQty());
		return l;
	}

	private void acceptOnlyUpToBookedQty() {
		try {
			service.acceptOnlyUpToBookedQty(quantitiesInCasesAndBottlesDialog.totalQtyInPieces());
		} catch (Exception e) {
			resetNodesOnError(e);
		}
	}
}
