package ph.txtdis.fx.dialog;

import java.util.List;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import ph.txtdis.dto.PickListDetail;
import ph.txtdis.fx.control.InputNode;
import ph.txtdis.service.LoadReturnService;

@Scope("prototype")
@Component("loadReturnDialog")
public class LoadReturnDialogImpl extends AbstractCasesAndBottlesReceivingDialog<LoadReturnService, PickListDetail>
		implements LoadReturnDialog {

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
