package ph.txtdis.mgdc.ccbpi.fx.pane;

import javafx.beans.binding.BooleanBinding;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.scene.Node;
import javafx.scene.layout.VBox;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import ph.txtdis.fx.control.AppCheckBox;
import ph.txtdis.fx.control.AppCombo;
import ph.txtdis.fx.control.LocalDatePicker;
import ph.txtdis.fx.pane.PricedItemPane;
import ph.txtdis.mgdc.ccbpi.fx.table.PricingTable;
import ph.txtdis.mgdc.ccbpi.service.BommedDiscountedPricedValidatedItemService;
import ph.txtdis.mgdc.fx.pane.AbstractItemPane;

import java.util.Arrays;
import java.util.List;

import static ph.txtdis.type.Type.ID;
import static ph.txtdis.type.Type.TEXT;

@Scope("prototype")
@Component("bommedItemPane")
public class PricedItemPaneImpl //
	extends AbstractItemPane<BommedDiscountedPricedValidatedItemService> //
	implements PricedItemPane {

	@Autowired
	private AppCombo<String> emptiesCombo;

	@Autowired
	private PricingTable pricingTable;

	@Autowired
	private LocalDatePicker endOfLifePicker;

	@Autowired
	private AppCheckBox emptiesCheckbox, notDiscountedCheckbox;

	private BooleanProperty hasAllUoms;

	@Override
	public void clear() {
		pricingTable.removeListener();
		qtyPerUomTable.removeListener();
	}

	protected VBox verticalPane() {
		return pane.topCenteredVertical(firstRowBox(), secondRowBox(), tableBox());
	}

	private Node firstRowBox() {
		return pane.centeredHorizontal(
			label.field("ID No."), idDisplay.readOnly().build(ID),
			label.name("Name"), nameField.length(12).width(180).build(TEXT),
			label.field("Description"), descriptionField.build(TEXT)
		);
	}

	private Node secondRowBox() {
		return pane.centeredHorizontal(
			notDiscountedCheckbox.label("NOT Discounted"),
			emptiesCheckbox.label("Empties"),
			emptiesCombo
		);
	}

	@Override
	public BooleanBinding hasIncompleteData() {
		return pricingTable.isEmpty();
	}

	@Override
	public void refresh() {
		super.refresh();
		emptiesCombo.items(service.listEmpties());
		notDiscountedCheckbox.setValue(service.isNotDiscounted());
		pricingTable.items(service.getPriceList());
	}

	@Override
	public void save() {
		super.save();
		updatePrices();
		if (!service.isNew())
			return;
		service.setNotDiscounted(notDiscountedCheckbox.getValue());
		service.setEmpties(emptiesCheckbox.getValue());
		service.setEmpties(emptiesCombo.getValue());
	}

	private void updatePrices() {
		service.setPriceList(pricingTable.getItems());
	}

	@Override
	public void setBindings() {
		super.setBindings();
		hasAllUoms = new SimpleBooleanProperty(false);
		vendorIdField.disableIf(isPosted());
		notDiscountedCheckbox.disableIf(isPosted()
			.or(vendorIdField.isEmpty()));
		emptiesCheckbox.disableIf(notDiscountedCheckbox.disabledProperty());
		qtyPerUomTable.disableIf(emptiesCheckbox.disabledProperty());
		pricingTable.disableIf(hasAllUoms.not());
	}

	@Override
	public void setListeners() {
		super.setListeners();
		pricingTable.setOnItemChange(e -> updatePrices());
	}

	@Override
	protected List<Node> tablePanes() {
		return Arrays.asList(qtyPerUomTablePane(), pricingTablePane());
	}

	private VBox pricingTablePane() {
		return pane.vertical(label.group("Pricing History"), pricingTable.build());
	}

	@Override
	protected void updateQtyPerUom() {
		super.updateQtyPerUom();
		hasAllUoms.set(service.hasReportUom());
	}
}
