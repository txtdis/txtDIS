package ph.txtdis.mgdc.gsm.fx.dialog;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import ph.txtdis.dto.ReceivingDetail;
import ph.txtdis.fx.control.InputNode;
import ph.txtdis.mgdc.fx.dialog.QuantitiesInCasesAndBottlesDialogImpl;
import ph.txtdis.mgdc.gsm.service.ReceivingService;

import java.util.List;

import static org.apache.log4j.Logger.getLogger;

public abstract class AbstractCasesAndBottlesReceivingDialog<AS extends ReceivingService, T extends ReceivingDetail> //
	extends AbstractReceivingDialog<AS, T> {

	private static Logger logger = getLogger(AbstractCasesAndBottlesReceivingDialog.class);

	@Autowired
	protected QuantitiesInCasesAndBottlesDialogImpl quantitiesInCasesAndBottlesDialog;

	@Override
	protected List<InputNode<?>> addNodes() {
		List<InputNode<?>> l = super.addNodes();
		l.addAll(quantitiesInCasesAndBottlesInputs());
		return l;
	}

	protected List<InputNode<?>> quantitiesInCasesAndBottlesInputs() {
		return quantitiesInCasesAndBottlesDialog.addNodes(service, "");
	}

	@Override
	protected T createEntity() {
		logger.info("\n    TotalQtyInPieces = " + quantitiesInCasesAndBottlesDialog.totalQtyInPieces());
		return service.updateReceivingDetailReturnedQty(quantitiesInCasesAndBottlesDialog.totalQtyInPieces());
	}
}
