package ph.txtdis.fx.pane;

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
import ph.txtdis.fx.control.AppField;
import ph.txtdis.fx.control.ErrorHandling;
import ph.txtdis.fx.control.LabelFactory;
import ph.txtdis.fx.dialog.MessageDialog;
import ph.txtdis.fx.table.QtyPerUomTable;
import ph.txtdis.service.ItemService;

public abstract class AbstractItemPane<AS extends ItemService> extends Pane implements ItemPane {

	@Autowired
	private MessageDialog dialog;

	@Autowired
	protected AppBoxPaneFactory box;

	@Autowired
	protected AppField<Long> idDisplay;

	@Autowired
	protected AppField<String> nameField, descriptionField, vendorIdField;

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
	public void refresh() {
		idDisplay.setValue(service.getId());
		nameField.setValue(service.getName());
		descriptionField.setValue(service.getDescription());
		vendorIdField.setValue(service.getVendorId());
		hasVendorId.set(service.getVendorId() != null);
		qtyPerUomTable.items(service.listQtyPerUom());
	}

	@Override
	public void save() {
		service.setVendorId(vendorIdField.getValue());
		if (!isNew())
			return;
		service.setName(nameField.getText());
		service.setDescription(descriptionField.getText());
	}

	@Override
	public void select() {
		if (isNew())
			nameField.requestFocus();
	}

	protected boolean isNew() {
		return service.isNew();
	}

	protected BooleanBinding posted() {
		return idDisplay.isNotEmpty();
	}

	protected VBox qtyPerUomTablePane() {
		return box.forVerticals(label.group("Qty per UOM Relative to 'EA'"), qtyPerUomTable.build());
	}

	private HBox tableBox() {
		return box.forHorizontalPane(tablePanes());
	}

	protected abstract List<? extends Node> tablePanes();

	protected void updateQtyPerUom() {
		service.setQtyPerUomList(qtyPerUomTable.getItems());
		hasNeededPurchaseUom.set(service.hasPurchaseUom());
	}

	private void validateName() {
		if (isNew())
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

	@Override
	public Pane get() {
		VBox vbox = box.forVerticals(gridPane(), tableBox());
		getChildren().add(vbox);
		return this;
	}

	protected GridPane gridPane() {
		gridPane.getChildren().clear();
		return gridPane;
	}

	@Override
	public void setBindings() {
		hasNeededPurchaseUom = new SimpleBooleanProperty(false);
		hasVendorId = new SimpleBooleanProperty(false);
		nameField.disableIf(posted());
		descriptionField.disableIf(nameField.isEmpty()//
				.or(posted()));
	}

	@Override
	public void setListeners() {
		nameField.setOnAction(e -> validateName());
		qtyPerUomTable.setOnItemChange(e -> updateQtyPerUom());
	}
}
