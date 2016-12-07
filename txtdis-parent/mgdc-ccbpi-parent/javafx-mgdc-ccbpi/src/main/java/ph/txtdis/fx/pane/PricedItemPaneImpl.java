package ph.txtdis.fx.pane;

import static ph.txtdis.type.Type.TEXT;

import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import javafx.beans.binding.BooleanBinding;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.scene.Node;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import ph.txtdis.fx.control.AppCombo;
import ph.txtdis.fx.table.PricingTable;
import ph.txtdis.service.EmptiesItemService;
import ph.txtdis.type.ItemType;
import ph.txtdis.type.Type;

@Lazy
@Component("pricedItemPane")
public class PricedItemPaneImpl extends AbstractItemPane<EmptiesItemService> implements PricedItemPane {

	@Autowired
	private AppCombo<String> emptiesCombo;

	@Autowired
	private PricingTable pricingTable;

	private BooleanProperty hasNeededSoldUom;

	@Override
	protected GridPane gridPane() {
		super.gridPane();
		gridPane.add(label.field("ID No."), 0, 0);
		gridPane.add(vendorIdField.build(Type.CODE), 1, 0);
		gridPane.add(label.name("Name"), 2, 0);
		gridPane.add(nameField.length(12).width(140).build(TEXT), 3, 0);
		gridPane.add(label.field("Description"), 4, 0);
		gridPane.add(descriptionField.width(360).build(TEXT), 5, 0);
		gridPane.add(label.field("Empties"), 6, 0);
		gridPane.add(emptiesCombo, 7, 0);
		return gridPane;
	}

	@Override
	public BooleanBinding hasIncompleteData() {
		return doesNotHaveNeededUoms().or(pricingTable.isEmpty());
	}

	@Override
	public void refresh() {
		super.refresh();
		pricingTable.items(service.getPriceList());
		emptiesCombo.items(service.listEmpties());
	}

	private VBox bomTablePane() {
		return box.forVerticals(label.group("Price List"), pricingTable.build());
	}

	@Override
	protected List<Node> tablePanes() {
		return Arrays.asList(qtyPerUomTablePane(), bomTablePane());
	}

	@Override
	public void setBindings() {
		super.setBindings();
		hasNeededSoldUom = new SimpleBooleanProperty(false);
		vendorIdField.disableIf(posted().and(hasVendorId));
		qtyPerUomTable.disableIf(posted().or(descriptionField.isEmpty()));
		emptiesCombo.disableIf(descriptionField.isEmpty());
		pricingTable.disableIf(doesNotHaveNeededUoms());
	}

	private BooleanBinding doesNotHaveNeededUoms() {
		return hasNeededPurchaseUom.not().or(hasNeededSoldUom.not());
	}

	@Override
	public void setListeners() {
		super.setListeners();
		vendorIdField.setOnAction(e -> comfirmVendorIdIsUnique());
		descriptionField.setOnAction(e -> service.setType(ItemType.PURCHASED));
		emptiesCombo.setOnAction(e -> service.setEmpties(emptiesCombo.getValue()));
		pricingTable.setOnItemChange(e -> service.setPriceList(pricingTable.getItems()));
	}

	private void comfirmVendorIdIsUnique() {
		if (isNew())
			try {
				//TODO
				//service.comfirmVendorIdIsUnique(vendorIdField.getValue());
			} catch (Exception e) {
				handleError(vendorIdField, e);
			}
	}

	@Override
	protected void updateQtyPerUom() {
		super.updateQtyPerUom();
		hasNeededSoldUom.set(service.hasSoldUom());
	}
}
