package ph.txtdis.fx.dialog;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import ph.txtdis.fx.control.InputNode;
import ph.txtdis.service.ItemInputtedService;

public abstract class AbstractAllItemInputDialog<AS extends ItemInputtedService<T>, T> extends AbstractFieldDialog<T>
		implements Inputted<T> {

	@Autowired
	protected ItemInputtedDialog itemInputtedDialog;

	@Autowired
	protected AS service;

	@Override
	protected List<InputNode<?>> addNodes() {
		return new ArrayList<>(itemNodes());
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
			resetNodesOnError(e);
		}
	}

	protected void updateIfItemIdIsValid() throws Exception {
		service.setItemUponValidation(itemInputtedDialog.getId());
		itemInputtedDialog.setName(service.getItemName());
	}

	@Override
	protected T createEntity() {
		return service.createDetail();
	}

	@Override
	protected String headerText() {
		return "Add New Item";
	}

	protected void setUomAndQtyUponValidation() {
		try {
			verifyQty();
		} catch (Exception e) {
			resetNodesOnError(e);
		}
	}

	protected abstract void verifyQty() throws Exception;
}
