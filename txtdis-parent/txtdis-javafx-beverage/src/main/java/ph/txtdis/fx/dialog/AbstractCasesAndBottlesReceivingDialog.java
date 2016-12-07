package ph.txtdis.fx.dialog;

import static org.apache.log4j.Logger.getLogger;

import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import ph.txtdis.dto.ReceivingDetail;
import ph.txtdis.fx.control.InputNode;
import ph.txtdis.service.ReceivingService;

public abstract class AbstractCasesAndBottlesReceivingDialog<AS extends ReceivingService, T extends ReceivingDetail>
		extends AbstractReceivingDialog<AS, T> {

	private static Logger logger = getLogger(AbstractCasesAndBottlesReceivingDialog.class);

	@Autowired
	protected QuantitiesInCasesAndBottlesDialog quantitiesInCasesAndBottlesDialog;

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
