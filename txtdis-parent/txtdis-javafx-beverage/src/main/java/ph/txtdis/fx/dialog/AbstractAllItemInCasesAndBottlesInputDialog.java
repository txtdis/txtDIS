package ph.txtdis.fx.dialog;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import ph.txtdis.fx.control.InputNode;
import ph.txtdis.service.ItemInputtedService;
import ph.txtdis.service.QtyPerUomService;
import ph.txtdis.service.QuantityValidated;
import ph.txtdis.type.UomType;

public abstract class AbstractAllItemInCasesAndBottlesInputDialog<AS extends ItemInputtedService<T>, T>
		extends AbstractAllItemInputDialog<AS, T> {

	@Autowired
	private QuantitiesInCasesAndBottlesDialog quantitiesInCasesAndBottlesDialog;

	@Override
	protected List<InputNode<?>> addNodes() {
		List<InputNode<?>> l = super.addNodes();
		l.addAll(quantitiesInCasesAndBottlesDialog.addNodes((QtyPerUomService) service, ""));
		quantitiesInCasesAndBottlesDialog.setQtyFieldOnAction(e -> setUomAndQtyUponValidation());
		return l;
	}

	@Override
	protected void verifyQty() throws Exception {
		((QuantityValidated) service).setQtyUponValidation(UomType.CS,
				quantitiesInCasesAndBottlesDialog.totalQtyInPieces());
	}
}
