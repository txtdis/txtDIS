package ph.txtdis.mgdc.ccbpi.fx.dialog;

import ph.txtdis.dto.ReceivingDetail;
import ph.txtdis.fx.control.InputNode;
import ph.txtdis.mgdc.ccbpi.service.UpToReturnableQtyReceivingService;

import java.util.List;

public abstract class AbstractUpToReturnableQtyCasesAndBottlesReceivingDialog<//
	AS extends UpToReturnableQtyReceivingService, //
	T extends ReceivingDetail> //
	extends AbstractCasesAndBottlesReceivingDialog<AS, T> {

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
