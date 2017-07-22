package ph.txtdis.mgdc.gsm.fx.dialog;

import static ph.txtdis.type.Type.ID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import ph.txtdis.app.SelectableListApp;
import ph.txtdis.app.Searchable;
import ph.txtdis.fx.control.LabeledField;
import ph.txtdis.fx.dialog.SearchDialog;
import ph.txtdis.mgdc.gsm.app.ItemListApp;
import ph.txtdis.mgdc.gsm.dto.Item;
import ph.txtdis.service.SearchedByNameService;

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
	public SelectableListApp<Item> getListApp() {
		return itemListApp;
	}

	@Override
	public SearchedByNameService<Item> getSearchableByNameService() {
		return itemService;
	}

	@Override
	public void nextFocus() {
		itemIdField.requestFocus();
	}
}
