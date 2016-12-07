package ph.txtdis.fx.dialog;

import static ph.txtdis.type.Type.ID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import ph.txtdis.app.AppSelectable;
import ph.txtdis.app.ItemListApp;
import ph.txtdis.app.Searchable;
import ph.txtdis.dto.Item;
import ph.txtdis.fx.control.LabeledField;
import ph.txtdis.service.ByNameSearchable;

@Scope("prototype")
@Component("itemInputtedDialog")
public class SearchableItemInputtedDialogImpl extends AbstractItemInputtedDialog implements Searchable<Item> {

	@Autowired
	private ItemListApp itemListApp;

	@Autowired
	private SearchDialog searchDialog;

	@Override
	protected LabeledField<Long> itemIdField() {
		itemIdField.name("Item No.").isSearchable().build(ID);
		itemIdField.setOnSearch(e -> openSearchDialog(inputDialog, errorDialog, searchDialog));
		return itemIdField;
	}

	@Override
	public AppSelectable<Item> getListApp() {
		return itemListApp;
	}

	@Override
	public ByNameSearchable<Item> getSearchableByNameService() {
		return itemService;
	}

	@Override
	public void nextFocus() {
		itemIdField.requestFocus();
	}
}
