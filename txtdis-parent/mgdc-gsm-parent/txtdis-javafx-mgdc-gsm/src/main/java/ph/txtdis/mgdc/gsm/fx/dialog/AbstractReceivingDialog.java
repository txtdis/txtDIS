package ph.txtdis.mgdc.gsm.fx.dialog;

import org.springframework.beans.factory.annotation.Autowired;
import ph.txtdis.dto.ReceivingDetail;
import ph.txtdis.fx.control.InputNode;
import ph.txtdis.fx.control.LabeledCombo;
import ph.txtdis.fx.dialog.AbstractFieldDialog;
import ph.txtdis.mgdc.fx.dialog.ReceivingDialog;
import ph.txtdis.mgdc.gsm.service.ReceivingService;

import java.util.ArrayList;
import java.util.List;

import static java.util.Arrays.asList;

public abstract class AbstractReceivingDialog<AS extends ReceivingService, T extends ReceivingDetail>
	extends AbstractFieldDialog<T>
	implements ReceivingDialog<T> {

	@Autowired
	protected LabeledCombo<String> itemCombo;

	@Autowired
	protected AS service;

	@Override
	public void refresh() {
		super.refresh();
		itemCombo.items(service.listReceivableItemNames());
	}

	@Override
	protected List<InputNode<?>> addNodes() {
		return new ArrayList<>(asList(itemCombo()));
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

	@Override
	protected String headerText() {
		return "Receive an Item";
	}
}
