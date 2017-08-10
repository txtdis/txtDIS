package ph.txtdis.mgdc.gsm.fx.dialog;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import ph.txtdis.app.Searchable;
import ph.txtdis.dto.Bom;
import ph.txtdis.fx.control.InputNode;
import ph.txtdis.fx.control.LabeledField;
import ph.txtdis.fx.dialog.AbstractFieldDialog;
import ph.txtdis.mgdc.gsm.app.ItemListApp;
import ph.txtdis.mgdc.gsm.dto.Item;
import ph.txtdis.mgdc.gsm.service.BommedDiscountedPricedValidatedItemService;

import java.math.BigDecimal;
import java.util.List;

import static java.util.Arrays.asList;
import static ph.txtdis.type.Type.*;
import static ph.txtdis.util.NumberUtils.isZero;

@Scope("prototype")
@Component("bomDialog")
public class BomDialog
	extends AbstractFieldDialog<Bom>
	implements Searchable<Item> {

	@Autowired
	private ItemListApp itemListApp;

	@Autowired
	private BommedDiscountedPricedValidatedItemService service;

	@Autowired
	private LabeledField<Long> itemIdField;

	@Autowired
	private LabeledField<String> itemNameDisplay;

	@Autowired
	private LabeledField<BigDecimal> qtyField;

	@Override
	public void nextFocus() {
		qtyField.requestFocus();
	}

	@Override
	public void updateUponVerification(Item t) {
		itemIdField.setValue(t.getId());
		updateNameAndUomUponVerificationIfInputted();
	}

	private void updateNameAndUomUponVerificationIfInputted() {
		try {
			service.setPartUponValidation(itemIdField.getValue());
			itemNameDisplay.setValue(service.getItemName());
		} catch (Exception e) {
			resetNodesOnError(e);
		}
	}

	@Override
	protected List<InputNode<?>> addNodes() {
		return asList(itemIdField(),
			itemNameDisplay.name("Description").readOnly().build(TEXT),
			qtyField());
	}

	private LabeledField<Long> itemIdField() {
		itemIdField.name("Item No.").isSearchable().build(ID);
		itemIdField.onAction(e -> updateNameAndUomUponVerificationIfInputted());
		itemIdField.setOnSearch(e -> openSearchDialog(service, this, itemListApp));
		return itemIdField;
	}

	private LabeledField<BigDecimal> qtyField() {
		qtyField.name("Quantity").build(QUANTITY);
		qtyField.onAction(e -> nullIfZero());
		return qtyField;
	}

	private void nullIfZero() {
		if (isZero(qtyField.getValue()))
			qtyField.clear();
	}

	@Override
	protected Bom createEntity() {
		return service.createBom(qtyField.getValue());
	}

	@Override
	protected String headerText() {
		return "Add New Part";
	}
}
