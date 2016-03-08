package ph.txtdis.fx.dialog;

import static java.util.Arrays.asList;
import static org.apache.log4j.Logger.getLogger;
import static ph.txtdis.type.Type.ID;
import static ph.txtdis.type.Type.INTEGER;
import static ph.txtdis.type.Type.TEXT;

import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import ph.txtdis.app.ItemApp;
import ph.txtdis.dto.ItemList;
import ph.txtdis.exception.NotFoundException;
import ph.txtdis.fx.control.InputNode;
import ph.txtdis.fx.control.LabeledField;
import ph.txtdis.service.BillingService;
import ph.txtdis.service.ItemService;

@Scope("prototype")
@Component("billingDialog")
public class BillingDialog extends FieldDialog<ItemList> {

	private static Logger logger = getLogger(BillingDialog.class);

	@Autowired
	private BillingService service;

	@Autowired
	private ItemApp itemApp;

	@Autowired
	private ItemService itemService;

	@Autowired
	private LabeledField<Long> itemIdInput;

	@Autowired
	private LabeledField<String> itemNameDisplay;

	@Autowired
	private LabeledField<Integer> caseField, bottleField;

	@Autowired
	private ItemDialog itemDialog;

	@Autowired
	private SearchDialog searchDialog;

	public void closeDialogs() {
		itemDialog.close();
		dialog.close();
		logger.info("Item @ addNewItem() = " + service.getItemCode() + " - " + service.getItemName());
	}

	public boolean inputtedItemId(Long id) {
		return itemNameDisplay.isEmpty().get() && id != 0;
	}

	private void addNewItem() {
		itemDialog.codeOf(itemIdInput.getValue()).addParent(this).start();
		service.setItem(itemDialog.getAddedItem());
		closeDialogs();
		updateItemFields();
	}

	private void displayItemListForSelection() {
		itemApp.addParent(this).start();
	}

	private LabeledField<Long> itemIdField() {
		itemIdInput.name("Item Code").isSearchable().build(ID);
		itemIdInput.setOnAction(e -> updateUponItemIdValidation(itemIdInput.getValue()));
		itemIdInput.setOnSearch(e -> searchItem());
		return itemIdInput;
	}

	private String openItemSearchDialog() {
		itemIdInput.setValue(null);
		searchDialog.criteria("name").addParent(this).start();
		return searchDialog.getText();
	}

	private void searchItem() {
		String name = openItemSearchDialog();
		if (name != null)
			searchItem(name);
	}

	private void searchItem(String name) {
		try {
			itemService.search(name);
			displayItemListForSelection();
			updateItemDisplaysPerSelection();
		} catch (Exception e) {
			resetNodesOnError(e);
		}
	}

	private void showOptionToAddNewItem(NotFoundException e) {
		dialog.showOption(e.getMessage() + "\nadd new?", "Add", "Close");
		dialog.setOnAction(a -> addNewItem());
		dialog.addParent(this).start();
	}

	private void updateItemDisplaysPerSelection() {
		service.setItem(itemApp.getSelection());
		updateItemFields();
	}

	private void updateItemFields() {
		itemNameDisplay.setValue(service.getItemName());
		itemIdInput.setValue(service.getItemCode());
	}

	private void updateUponItemIdValidation(Long id) {
		if (inputtedItemId(id))
			try {
				service.setItemUponValidation(id);
				updateItemFields();
			} catch (NotFoundException e) {
				showOptionToAddNewItem(e);
				updateItemFields();
			} catch (Exception e) {
				resetNodesOnError(e);
			}
	}

	@Override
	protected List<InputNode<?>> addNodes() {
		return asList(//
				itemIdField(), //
				itemNameDisplay.name("Description").readOnly().build(TEXT), //
				caseField.name("No. of Cases").build(INTEGER), //
				bottleField.name("No. of Bottles").build(INTEGER));
	}

	@Override
	protected ItemList createEntity() {
		return service.createDetail(caseField.getValue(), bottleField.getValue());
	}

	@Override
	protected String headerText() {
		return "Append Particulars";
	}
}