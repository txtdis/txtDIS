package ph.txtdis.mgdc.fx.pane;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import javafx.beans.binding.BooleanBinding;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.scene.Node;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import ph.txtdis.fx.control.AppFieldImpl;
import ph.txtdis.fx.control.ErrorHandling;
import ph.txtdis.fx.control.LabelFactory;
import ph.txtdis.fx.dialog.MessageDialog;
import ph.txtdis.fx.pane.AppBoxPaneFactory;
import ph.txtdis.fx.pane.AppGridPane;
import ph.txtdis.fx.pane.ItemPane;
import ph.txtdis.mgdc.fx.table.QtyPerUomTable;
import ph.txtdis.mgdc.service.ItemService;

public abstract class AbstractItemPane<AS extends ItemService> //
		extends Pane //
		implements ItemPane {

	@Autowired
	private MessageDialog dialog;

	@Autowired
	protected AppBoxPaneFactory box;

	@Autowired
	protected AppFieldImpl<Long> idDisplay;

	@Autowired
	protected AppFieldImpl<String> nameField, descriptionField, vendorIdField;

	@Autowired
	protected AppGridPane gridPane;

	@Autowired
	protected LabelFactory label;

	@Autowired
	protected QtyPerUomTable qtyPerUomTable;

	@Autowired
	protected AS service;

	protected BooleanProperty hasNeededPurchaseUom, hasVendorId;

	@Override
	public Pane get() {
		getChildren().setAll(verticalPane());
		return this;
	}

	protected VBox verticalPane() {
		return box.forVerticalPane(gridPane(), tableBox());
	}

	protected GridPane gridPane() {
		gridPane.getChildren().clear();
		return gridPane;
	}

	protected HBox tableBox() {
		return box.forHorizontalPane(tablePanes());
	}

	protected abstract List<? extends Node> tablePanes();

	@Override
	public void refresh() {
		idDisplay.setValue(service.getId());
		nameField.setValue(service.getName());
		descriptionField.setValue(service.getDescription());
		vendorIdField.setValue(service.getVendorNo());
		hasVendorId.set(service.getVendorNo() != null);
		qtyPerUomTable.items(service.listQtyPerUom());
	}

	@Override
	public void save() {
		service.setVendorId(vendorIdField.getValue());
		if (!service.isNew())
			return;
		service.setName(nameField.getText());
		service.setDescription(descriptionField.getText());
	}

	@Override
	public void select() {
		if (service.isNew())
			nameField.requestFocus();
	}

	@Override
	public void setFocus() {
		nameField.requestFocus();
	}

	@Override
	public void setBindings() {
		hasNeededPurchaseUom = new SimpleBooleanProperty(false);
		hasVendorId = new SimpleBooleanProperty(false);
		nameField.disableIf(isPosted());
		descriptionField.disableIf(nameField.isEmpty()//
				.or(isPosted()));
	}

	protected BooleanBinding isPosted() {
		return idDisplay.isNotEmpty();
	}

	@Override
	public void setListeners() {
		nameField.onAction(e -> validateName());
		qtyPerUomTable.setOnItemChange(e -> updateQtyPerUom());
	}

	private void validateName() {
		if (service.isNew())
			try {
				service.setNameIfUnique(nameField.getValue());
			} catch (Exception e) {
				handleError(nameField, e);
			}
	}

	protected void handleError(ErrorHandling control, Exception e) {
		dialog.show(e).addParent(this).start();
		control.handleError();
	}

	protected void updateQtyPerUom() {
		service.setQtyPerUomList(qtyPerUomTable.getItems());
		hasNeededPurchaseUom.set(service.hasPurchaseUom());
	}

	protected BooleanBinding isNew() {
		return idDisplay.isEmpty();
	}

	protected VBox qtyPerUomTablePane() {
		return box.forVerticals( //
				label.group("Qty per UOM of Smallest SKU"), //
				qtyPerUomTable.build());
	}
}
