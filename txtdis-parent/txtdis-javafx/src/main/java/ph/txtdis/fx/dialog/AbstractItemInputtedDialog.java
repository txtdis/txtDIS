package ph.txtdis.fx.dialog;

import static java.util.Arrays.asList;
import static ph.txtdis.type.Type.ID;
import static ph.txtdis.type.Type.TEXT;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import ph.txtdis.dto.Item;
import ph.txtdis.fx.control.InputNode;
import ph.txtdis.fx.control.LabeledField;
import ph.txtdis.service.ItemService;

public abstract class AbstractItemInputtedDialog implements ItemInputtedDialog {

	@Autowired
	protected LabeledField<String> itemNameDisplay;

	@Autowired
	protected LabeledField<Long> itemIdField;

	@Autowired
	protected MessageDialog errorDialog;

	@Autowired
	protected ItemService itemService;

	protected AbstractInputDialog inputDialog;

	@Override
	public List<InputNode<?>> addNodes(AbstractInputDialog inputDialog) {
		this.inputDialog = inputDialog;
		return asList(itemIdField(), itemNameDisplay());
	}

	protected LabeledField<Long> itemIdField() {
		return itemIdField.name("Item No.").build(ID);
	}

	private LabeledField<String> itemNameDisplay() {
		return itemNameDisplay.name("Description").readOnly().build(TEXT);
	}

	@Override
	public Long getId() {
		return itemIdField.getValue();
	}

	@Override
	public InputNode<String> getItemInput() {
		return itemNameDisplay;
	}

	@Override
	public void nextFocus() {
		itemIdField.requestFocus();
	}

	@Override
	public void refresh() {
		itemIdField.reset();
		itemNameDisplay.reset();
	}

	@Override
	public void setItemIdFieldOnAction(EventHandler<ActionEvent> e) {
		if (itemIdField != null)
			itemIdField.setOnAction(e);
	}

	@Override
	public void setName(String name) {
		itemNameDisplay.setValue(name);
	}

	@Override
	public void updateUponVerification(Item t) {
		itemIdField.setValue(t.getId());
	}

	@Override
	public Item validateItemExists() throws Exception {
		Item i = itemService.find(getId());
		itemNameDisplay.setValue(i.getName());
		return i;
	}
}
