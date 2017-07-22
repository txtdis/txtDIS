package ph.txtdis.mgdc.ccbpi.fx.dialog;

import static java.util.Arrays.asList;
import static org.apache.log4j.Logger.getLogger;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import ph.txtdis.dto.ReceivingDetail;
import ph.txtdis.fx.control.InputNode;
import ph.txtdis.fx.control.LabeledCombo;
import ph.txtdis.fx.dialog.AbstractFieldDialog;
import ph.txtdis.mgdc.ccbpi.service.ReceivingService;
import ph.txtdis.mgdc.fx.dialog.QuantitiesInCasesAndBottlesDialogImpl;
import ph.txtdis.mgdc.fx.dialog.ReceivingDialog;

public abstract class AbstractCasesAndBottlesReceivingDialog< //
		AS extends ReceivingService, //
		T extends ReceivingDetail> //
		extends AbstractFieldDialog<T> //
		implements ReceivingDialog<T> {

	private static Logger logger = getLogger(AbstractCasesAndBottlesReceivingDialog.class);

	@Autowired
	protected LabeledCombo<String> itemCombo;

	@Autowired
	protected AS service;

	@Autowired
	protected QuantitiesInCasesAndBottlesDialogImpl quantitiesInCasesAndBottlesDialog;

	@Override
	protected List<InputNode<?>> addNodes() {
		List<InputNode<?>> l = new ArrayList<>(asList(itemCombo()));
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

	@Override
	protected String headerText() {
		return "Receive an Item";
	}

	@Override
	public void refresh() {
		super.refresh();
		itemCombo.items(service.listReceivableItemNames());
	}

	protected LabeledCombo<String> itemCombo() {
		itemCombo.name("Item").build();
		itemCombo.onAction(e -> setReceivingDetailAndItsItem());
		return itemCombo;
	}

	private void setReceivingDetailAndItsItem() {
		try {
			service.setReceivingDetailAndItsItem(itemCombo.getValue());
		} catch (Exception e) {
			resetNodesOnError(e);
		}
	}
}
