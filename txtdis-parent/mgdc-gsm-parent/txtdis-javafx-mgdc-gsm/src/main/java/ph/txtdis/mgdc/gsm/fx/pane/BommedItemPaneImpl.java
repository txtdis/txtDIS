package ph.txtdis.mgdc.gsm.fx.pane;

import javafx.beans.binding.BooleanBinding;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.scene.Node;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import ph.txtdis.dto.ItemFamily;
import ph.txtdis.fx.control.AppCheckBox;
import ph.txtdis.fx.control.AppCombo;
import ph.txtdis.fx.control.LocalDatePicker;
import ph.txtdis.fx.pane.BommedItemPane;
import ph.txtdis.mgdc.fx.pane.AbstractItemPane;
import ph.txtdis.mgdc.gsm.fx.table.BomTable;
import ph.txtdis.mgdc.gsm.service.BommedDiscountedPricedValidatedItemService;
import ph.txtdis.type.ItemType;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import static javafx.beans.binding.Bindings.when;
import static ph.txtdis.type.ItemType.*;
import static ph.txtdis.type.Type.ID;
import static ph.txtdis.type.Type.TEXT;

@Scope("prototype")
@Component("bommedItemPane")
public class BommedItemPaneImpl //
	extends AbstractItemPane<BommedDiscountedPricedValidatedItemService> //
	implements BommedItemPane {

	@Autowired
	private AppCombo<ItemType> typeCombo;

	@Autowired
	private AppCombo<ItemFamily> familyCombo;

	@Autowired
	private BomTable bomTable;

	@Autowired
	private LocalDatePicker endOfLifePicker;

	@Autowired
	private AppCheckBox notDiscountedCheckbox;

	private BooleanProperty hasNeededReportUom;

	@Override
	public void clear() {
		bomTable.removeListener();
	}

	@Override
	protected GridPane gridPane() {
		super.gridPane();
		gridPane.add(label.field("ID No."), 0, 0);
		gridPane.add(idDisplay.readOnly().build(ID), 1, 0);
		gridPane.add(label.name("Name"), 2, 0);
		gridPane.add(nameField.length(12).width(180).build(TEXT), 3, 0, 2, 1);
		gridPane.add(label.field("Description"), 5, 0);
		gridPane.add(descriptionField.width(320).build(TEXT), 6, 0, 4, 1);
		gridPane.add(label.field("Type"), 0, 1);
		gridPane.add(typeCombo.items(service.listTypes()), 1, 1, 2, 1);
		gridPane.add(label.field("Family"), 3, 1);
		gridPane.add(familyCombo, 4, 1);
		gridPane.add(label.field("Vendor ID"), 5, 1);
		gridPane.add(vendorIdField.build(TEXT).width(180), 6, 1);
		gridPane.add(label.field("End of Life"), 7, 1);
		gridPane.add(endOfLifePicker, 8, 1);
		gridPane.add(notDiscountedCheckbox.label("Customer Discount NOT Applied"), 9, 1);
		return gridPane;
	}

	@Override
	public BooleanBinding hasIncompleteData() {
		return doesNotHaveNeededUoms() //
			.or(bomTable.isEmpty()//
				.and(typeCombo.are(BUNDLED, FREE, PROMO, REPACKED)));
	}

	private BooleanBinding doesNotHaveNeededUoms() {
		return hasNeededReportUom.not()//
			.or(isPurchased().and(hasNeededPurchaseUom.not()));
	}

	private BooleanBinding isPurchased() {
		return typeCombo.is(PURCHASED);
	}

	@Override
	public BooleanBinding needsPrice() {
		return typeCombo.isNot(FREE);
	}

	@Override
	public void refresh() {
		super.refresh();
		typeCombo.select(service.getType());
		familyCombo.items(service.listParents());
		refreshEndOfLifePicker();
		notDiscountedCheckbox.setValue(service.isNotDiscounted());
		bomTable.items(service.listBoms());
	}

	private void refreshEndOfLifePicker() {
		LocalDate eol = service.getEndOfLife();
		setEndOfLifePrompt(eol);
		endOfLifePicker.setValue(eol);
	}

	private void setEndOfLifePrompt(LocalDate eol) {
		if (!service.isNew() && eol == null)
			endOfLifePicker.setPromptText(null);
		else
			endOfLifePicker.setPromptText("08/08/2008");
	}

	@Override
	public void save() {
		super.save();
		service.setEndOfLife(endOfLifePicker.getValue());
		service.setType(typeCombo.getValue());
		updateBom();
		if (!service.isNew())
			return;
		service.setFamily(familyCombo.getValue());
		service.setNotDiscounted(notDiscountedCheckbox.getValue());
	}

	private void updateBom() {
		service.setBoms(bomTable.getItems());
	}

	@Override
	public void setBindings() {
		super.setBindings();
		hasNeededReportUom = new SimpleBooleanProperty(false);
		vendorIdField.disableIf(isNew() //
			.and(familyCombo.isEmpty() //
				.or(isPurchased().not()))); //
		endOfLifePicker.disableIf(isPurchased() //
			.and(vendorIdField.isEmpty()));
		notDiscountedCheckbox.disableIf(endOfLifePicker.disabledProperty()//
			.or(isFree()));
		qtyPerUomTable.disableIf(when(isFree()) //
			.then(endOfLifePicker.disabledProperty()) //
			.otherwise(notDiscountedCheckbox.disabledProperty()));
		bomTable.disableIf(doesNotHaveNeededUoms() //
			.or(isPurchased()));
	}

	private BooleanBinding isFree() {
		return typeCombo.is(FREE);
	}

	@Override
	public void setListeners() {
		super.setListeners();
		typeCombo.onAction(e -> setTypeAfterClearingTypeDependentControls());
		bomTable.setOnItemChange(e -> updateBom());
	}

	private void setTypeAfterClearingTypeDependentControls() {
		if (service.isNew()) {
			vendorIdField.setValue(null);
			notDiscountedCheckbox.setValue(null);
			qtyPerUomTable.items(null);
			bomTable.items(null);
			service.setType(typeCombo.getValue());
		}
	}

	@Override
	protected List<Node> tablePanes() {
		return Arrays.asList(qtyPerUomTablePane(), bomTablePane());
	}

	private VBox bomTablePane() {
		return pane.vertical(label.group("Bill of Materials"), bomTable.build());
	}

	@Override
	protected void updateQtyPerUom() {
		super.updateQtyPerUom();
		hasNeededReportUom.set(service.hasReportUom());
	}
}
