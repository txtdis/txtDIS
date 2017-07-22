package ph.txtdis.mgdc.ccbpi.fx.pane;

import static javafx.beans.binding.Bindings.when;
import static ph.txtdis.type.ItemType.BUNDLED;
import static ph.txtdis.type.ItemType.FREE;
import static ph.txtdis.type.ItemType.PROMO;
import static ph.txtdis.type.ItemType.PURCHASED;
import static ph.txtdis.type.ItemType.REPACKED;
import static ph.txtdis.type.Type.ID;
import static ph.txtdis.type.Type.TEXT;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javafx.beans.binding.BooleanBinding;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ObservableBooleanValue;
import javafx.scene.Node;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import ph.txtdis.dto.ItemFamily;
import ph.txtdis.fx.control.AppCheckBox;
import ph.txtdis.fx.control.AppCombo;
import ph.txtdis.fx.control.LocalDatePicker;
import ph.txtdis.fx.pane.PricedItemPane;
import ph.txtdis.mgdc.ccbpi.fx.table.PricingTable;
import ph.txtdis.mgdc.ccbpi.service.BommedDiscountedPricedValidatedItemService;
import ph.txtdis.mgdc.fx.pane.AbstractItemPane;
import ph.txtdis.type.ItemType;

@Scope("prototype")
@Component("bommedItemPane")
public class PricedItemPaneImpl //
		extends AbstractItemPane<BommedDiscountedPricedValidatedItemService> //
		implements PricedItemPane {

	@Autowired
	private AppCombo<ItemType> typeCombo;

	@Autowired
	private AppCombo<ItemFamily> familyCombo;

	@Autowired
	private PricingTable pricingTable;

	@Autowired
	private LocalDatePicker endOfLifePicker;

	@Autowired
	private AppCheckBox notDiscountedCheckbox;

	private BooleanProperty hasNeededReportUom;

	@Override
	public void clear() {
		pricingTable.removeListener();
		qtyPerUomTable.removeListener();
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
				.or(pricingTable.isEmpty()//
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
	public void refresh() {
		super.refresh();
		typeCombo.select(service.getType());
		refreshEndOfLifePicker();
		notDiscountedCheckbox.setValue(service.isNotDiscounted());
		pricingTable.items(service.getPriceList());
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
		updatePrices();
		if (!service.isNew())
			return;
		service.setFamily(familyCombo.getValue());
		service.setNotDiscounted(notDiscountedCheckbox.getValue());
	}

	private void updatePrices() {
		service.setPriceList(pricingTable.getItems());
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
		pricingTable.disableIf(doesNotHaveNeededUoms() //
				.or(isPurchased()));
	}

	private ObservableBooleanValue isFree() {
		return typeCombo.is(FREE);
	}

	@Override
	public void setListeners() {
		super.setListeners();
		typeCombo.onAction(e -> setTypeAfterClearingTypeDependentControls());
		pricingTable.setOnItemChange(e -> updatePrices());
	}

	private void setTypeAfterClearingTypeDependentControls() {
		if (service.isNew()) {
			vendorIdField.setValue(null);
			notDiscountedCheckbox.setValue(null);
			qtyPerUomTable.items(null);
			pricingTable.items(null);
			service.setType(typeCombo.getValue());
		}
	}

	@Override
	protected List<Node> tablePanes() {
		return Arrays.asList(qtyPerUomTablePane(), bomTablePane());
	}

	private VBox bomTablePane() {
		return box.forVerticals(label.group("Bill of Materials"), pricingTable.build());
	}

	@Override
	protected void updateQtyPerUom() {
		super.updateQtyPerUom();
		hasNeededReportUom.set(service.hasReportUom());
	}
}