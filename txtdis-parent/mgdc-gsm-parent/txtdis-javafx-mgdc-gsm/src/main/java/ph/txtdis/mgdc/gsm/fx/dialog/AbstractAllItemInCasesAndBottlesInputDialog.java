package ph.txtdis.mgdc.gsm.fx.dialog;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import ph.txtdis.fx.control.InputNode;
import ph.txtdis.mgdc.fx.dialog.QuantitiesInCasesAndBottlesDialogImpl;
import ph.txtdis.service.ItemInputtedService;
import ph.txtdis.service.QtyPerUomService;
import ph.txtdis.service.QuantityValidated;
import ph.txtdis.type.UomType;

import java.util.List;

import static org.apache.log4j.Logger.getLogger;

public abstract class AbstractAllItemInCasesAndBottlesInputDialog<AS extends ItemInputtedService<T>, T> //
	extends AbstractAllItemInputDialog<AS, T> {

	private static Logger logger = getLogger(AbstractAllItemInCasesAndBottlesInputDialog.class);

	@Autowired
	private QuantitiesInCasesAndBottlesDialogImpl quantitiesInCasesAndBottlesDialog;

	@Override
	protected List<InputNode<?>> addNodes() {
		logger.info("\n    addNodes()");
		List<InputNode<?>> l = super.addNodes();
		l.addAll(quantitiesInCasesAndBottlesDialog.addNodes((QtyPerUomService) service, ""));
		quantitiesInCasesAndBottlesDialog.setQtyFieldOnAction(e -> setUomAndQtyUponValidation());
		return l;
	}

	@Override
	protected void verifyQty() throws Exception {
		logger.info("\n    verifyQty()(");
		((QuantityValidated) service)
			.setQtyUponValidation(UomType.CS, quantitiesInCasesAndBottlesDialog.totalQtyInPieces());
	}
}
