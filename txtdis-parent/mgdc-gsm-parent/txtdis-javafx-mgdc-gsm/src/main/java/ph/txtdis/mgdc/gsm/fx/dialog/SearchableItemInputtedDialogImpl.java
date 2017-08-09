package ph.txtdis.mgdc.gsm.fx.dialog;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import ph.txtdis.app.Searchable;
import ph.txtdis.fx.control.LabeledField;
import ph.txtdis.fx.dialog.SearchDialog;
import ph.txtdis.mgdc.gsm.app.ItemListApp;
import ph.txtdis.mgdc.gsm.dto.Item;

import static ph.txtdis.type.Type.ID;

@Scope("prototype")
@Component("itemInputtedDialog")
public class SearchableItemInputtedDialogImpl
	extends AbstractItemInputtedDialog
	implements Searchable<Item> {

	@Autowired
	private ItemListApp itemListApp;

	@Autowired
	private SearchDialog searchDialog;

	@Override
	protected LabeledField<Long> itemIdField() {
		itemIdField.name("Item No.").isSearchable().build(ID);
		itemIdField.setOnSearch(e -> openSearchDialog(itemService, inputDialog, itemListApp, errorDialog, searchDialog));
		return itemIdField;
	}

	@Override
	public void nextFocus() {
		itemIdField.requestFocus();
	}
}
