package ph.txtdis.fx.dialog;

import java.util.List;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import ph.txtdis.dto.BillableDetail;
import ph.txtdis.fx.control.InputNode;
import ph.txtdis.service.CokeBillableService;

@Scope("prototype")
@Component("receivingDialog")
public class ReceivingDialogImpl extends AbstractCasesAndBottlesReceivingDialog<CokeBillableService, BillableDetail>
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
