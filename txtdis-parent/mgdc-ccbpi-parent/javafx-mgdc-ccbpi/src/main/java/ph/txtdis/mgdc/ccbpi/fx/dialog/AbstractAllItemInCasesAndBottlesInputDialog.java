package ph.txtdis.mgdc.ccbpi.fx.dialog;

import static org.apache.log4j.Logger.getLogger;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import ph.txtdis.fx.control.InputNode;
import ph.txtdis.fx.dialog.AbstractFieldDialog;
import ph.txtdis.fx.dialog.InputtedDialog;
import ph.txtdis.mgdc.fx.dialog.QuantitiesInCasesAndBottlesDialogImpl;
import ph.txtdis.service.ItemInputtedService;
import ph.txtdis.service.QtyPerUomService;
import ph.txtdis.service.QuantityValidated;
import ph.txtdis.type.UomType;

public abstract class AbstractAllItemInCasesAndBottlesInputDialog<AS extends ItemInputtedService<T>, T> //
		extends AbstractFieldDialog<T> //
		implements InputtedDialog<T> {

	private static Logger logger = getLogger(AbstractAllItemInCasesAndBottlesInputDialog.class);

	@Autowired
	private QuantitiesInCasesAndBottlesDialogImpl quantitiesInCasesAndBottlesDialog;

	@Autowired
	protected ItemInputtedDialog itemInputtedDialog;

	@Autowired
	protected AS service;

	@Override
	protected String headerText() {
		return "Add New Item";
	}

	@Override
	protected List<InputNode<?>> addNodes() {
		List<InputNode<?>> l = new ArrayList<>(itemNodes());
		l.addAll(quantitiesInCasesAndBottlesDialog.addNodes((QtyPerUomService) service, ""));
		quantitiesInCasesAndBottlesDialog.setQtyFieldOnAction(e -> setUomAndQtyUponValidation());
		return l;
	}

	private List<InputNode<?>> itemNodes() {
		List<InputNode<?>> l = itemInputtedDialog.addNodes(this);
		itemInputtedDialog.setItemIdFieldOnAction(e -> updateUponItemIdVerification());
		return l;
	}

	private void updateUponItemIdVerification() {
		try {
			updateIfItemIdIsValid();
		} catch (Exception e) {
			e.printStackTrace();
			resetNodesOnError(e);
		}
	}

	private void updateIfItemIdIsValid() throws Exception {
		service.setItemUponValidation(itemInputtedDialog.getId());
		itemInputtedDialog.setName(service.getItemName());
	}

	@Override
	protected T createEntity() {
		return service.createDetail();
	}

	private void setUomAndQtyUponValidation() {
		try {
			verifyQty();
		} catch (Exception e) {
			resetNodesOnError(e);
		}
	}

	private void verifyQty() throws Exception {
		logger.info("\n    verifyQty()(");
		((QuantityValidated) service).setQtyUponValidation(UomType.CS, quantitiesInCasesAndBottlesDialog.totalQtyInPieces());
	}
}
